package io.github.bvilii.yapper.client.backend.service.chat;

import io.github.bvilii.yapper.client.backend.network.ServerConnection;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.RoomHistoryRequest;
import io.github.bvilii.yapper.common.message.client.SendChat;
import io.github.bvilii.yapper.common.message.server.ChatMessage;
import io.github.bvilii.yapper.common.message.server.RoomHistory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatService implements ChatSubject{
    private final MessageCodec codec;
    private final ServerConnection connection;
    private final List<ChatObserver> observers = new CopyOnWriteArrayList<>();

    public ChatService(MessageCodec codec, ServerConnection connection) {
        this.codec = codec;
        this.connection = connection;
    }

    public void sendChat(long roomId, String text){
        connection.writeToServer(codec.encode(new SendChat(roomId, text)));
    }

    public void handleChatArrived(ChatMessage chat) {
        for (ChatObserver observer : observers){
            observer.onMessageArrived(chat.message());
        }
    }

    @Override
    public void addObserver(ChatObserver observer) {
        observers.add(observer);
    }

    public void requestRoomHistory(long id) {
        connection.writeToServer(codec.encode(new RoomHistoryRequest(id)));
    }

    public void handleRoomHistory(RoomHistory history) {
        for (ChatObserver observer : observers){
            observer.onHistoryLoaded(history.roomId(), history.messages());
        }
    }
}
