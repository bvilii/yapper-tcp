package io.github.bvilii.yapper.client.backend.controller;

import io.github.bvilii.yapper.client.backend.service.auth.AuthService;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.common.message.InvalidMessageException;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.server.*;

public class MessageController {
    private final MessageCodec codec;
    private final AuthService authService;
    private final RoomService roomService;

    public MessageController(MessageCodec codec, AuthService authService, RoomService roomService) {
        this.codec = codec;
        this.authService = authService;
        this.roomService = roomService;
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

            case ChatMessage chatMessage -> {
            }
            case ErrorMessage errorMessage -> {
            }
        }
    }
}
