package io.github.bvilii.yapper.client.backend.controller;

import io.github.bvilii.yapper.client.backend.service.auth.AuthService;
import io.github.bvilii.yapper.client.backend.service.chat.ChatService;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.common.message.InvalidMessageException;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.server.*;

public class MessageController {
    private final MessageCodec codec;
    private final AuthService authService;
    private final RoomService roomService;
    private final ChatService chatService;

    public MessageController(MessageCodec codec, AuthService authService, RoomService roomService, ChatService chatService) {
        this.codec = codec;
        this.authService = authService;
        this.roomService = roomService;
        this.chatService = chatService;
    }

    public void handleMessage(String line) {
        ServerMessage message;
        try {
            message = codec.decodeServerMessage(line);
        } catch (InvalidMessageException e) {
            return;
        }

        switch (message) {
            case RegisterOk m -> authService.handleRegisterSuccess(m);
            case RegisterFailed m -> authService.handleRegisterFailure(m);
            case LoginFailed m -> authService.handleLoginFailure(m);
            case LoginOk m -> authService.handleLoginSuccess(m);
            case CreateRoomFailed m -> roomService.handleCreateRoomFailed(m);
            case RoomCreated m -> roomService.handleRoomCreated(m);
            case RoomList m -> roomService.handleRoomList(m);
            case DmOpened m -> roomService.handleDmOpened(m);
            case OpenDmFailed m -> roomService.handleOpenDmFailed(m);
            case RoomHistory m -> chatService.handleRoomHistory(m);
            case ChatMessage m -> chatService.handleChatArrived(m);
            case ErrorMessage m -> {
            }
            case RoomDeleted m -> roomService.handleRoomDeleted(m);
        }
    }
}