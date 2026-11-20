package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.RoomHistoryRequest;
import io.github.bvilii.yapper.common.message.client.SendChat;
import io.github.bvilii.yapper.common.message.server.ChatMessage;
import io.github.bvilii.yapper.common.message.server.ErrorMessage;
import io.github.bvilii.yapper.common.message.server.RoomHistory;
import io.github.bvilii.yapper.common.model.MessageInfo;
import io.github.bvilii.yapper.server.controller.ClientHandler;
import io.github.bvilii.yapper.server.persistence.Message;
import io.github.bvilii.yapper.server.persistence.MessageRepository;
import io.github.bvilii.yapper.server.persistence.MessageWithSender;
import io.github.bvilii.yapper.server.persistence.RoomRepository;
import io.github.bvilii.yapper.server.persistence.User;

import java.util.List;
import java.util.Set;

public class ChatService {
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final int HISTORY_LIMIT = 20;
    private static final String DELETED_USER = "deleted user";

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final ClientRegistry registry;
    private final MessageCodec codec;

    public ChatService(RoomRepository roomRepository, MessageRepository messageRepository,
                       ClientRegistry registry, MessageCodec codec) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.registry = registry;
        this.codec = codec;
    }

    public void handleSendChat(SendChat request, ClientHandler sender) {
        User user = sender.getUser();
        if (user == null) {
            return;
        }

        String text = request.text() == null ? "" : request.text().strip();
        if (text.isEmpty()) {
            fail(sender, "Message must not be empty");
            return;
        }
        if (text.length() > MAX_MESSAGE_LENGTH) {
            fail(sender, "Message must be at most " + MAX_MESSAGE_LENGTH + " characters");
            return;
        }

        Set<Long> memberIds = roomRepository.findMemberIds(request.roomId());
        if (!memberIds.contains(user.id())) {
            fail(sender, "You are not a member of this room");
            return;
        }

        Message saved = messageRepository.save(request.roomId(), user.id(), text);

        MessageInfo info = new MessageInfo(
                saved.id(),
                saved.roomId(),
                user.username(),
                saved.content(),
                saved.createdAt().toEpochMilli()
        );
        String json = codec.encode(new ChatMessage(info));
        for (ClientHandler client : registry.getOnlineClients(memberIds)) {
            client.send(json);
        }
    }

    public void handleRoomHistoryRequest(RoomHistoryRequest request, ClientHandler sender) {
        User user = sender.getUser();
        if (user == null) {
            return;
        }

        Set<Long> memberIds = roomRepository.findMemberIds(request.roomId());
        if (!memberIds.contains(user.id())) {
            fail(sender, "You are not a member of this room");
            return;
        }

        List<MessageInfo> messages = messageRepository.findLatestByRoom(request.roomId(), HISTORY_LIMIT)
                .stream()
                .map(this::toMessageInfo)
                .toList();

        sender.send(codec.encode(new RoomHistory(request.roomId(), messages)));
    }

    private MessageInfo toMessageInfo(MessageWithSender found) {
        Message message = found.message();
        String senderName = found.senderName() != null ? found.senderName() : DELETED_USER;
        return new MessageInfo(
                message.id(),
                message.roomId(),
                senderName,
                message.content(),
                message.createdAt().toEpochMilli()
        );
    }

    private void fail(ClientHandler sender, String reason) {
        sender.send(codec.encode(new ErrorMessage(reason)));
    }
}