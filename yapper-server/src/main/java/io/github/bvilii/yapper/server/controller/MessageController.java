package io.github.bvilii.yapper.server.controller;

import io.github.bvilii.yapper.common.message.InvalidMessageException;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.*;
import io.github.bvilii.yapper.common.message.client.LoginRequest;
import io.github.bvilii.yapper.common.message.client.RegisterRequest;
import io.github.bvilii.yapper.common.message.client.TokenLoginRequest;
import io.github.bvilii.yapper.common.message.client.SendChat;
import io.github.bvilii.yapper.common.message.server.ErrorMessage;
import io.github.bvilii.yapper.server.service.AuthService;
import io.github.bvilii.yapper.server.service.ChatService;
import io.github.bvilii.yapper.server.service.RoomService;

public class MessageController {
    private final MessageCodec codec;
    private final ChatService chatService;
    private final AuthService authService;
    private final RoomService roomService;

    public MessageController(MessageCodec codec, ChatService chatService, AuthService authService, RoomService roomService) {
        this.codec = codec;
        this.chatService = chatService;
        this.authService = authService;
        this.roomService = roomService;
    }

    public void handleMessage(String line, ClientHandler sender) {
        ClientMessage message;
        try {
            message = codec.decodeClientMessage(line);
        } catch (InvalidMessageException e) {
            sender.send(codec.encode(new ErrorMessage("Invalid message")));
            return;
        }

        boolean isAuthMessage = message instanceof RegisterRequest
                || message instanceof LoginRequest
                || message instanceof TokenLoginRequest;

        if (!sender.isAuthenticated() && !isAuthMessage) {
            sender.send(codec.encode(new ErrorMessage("Not authenticated")));
            return;
        }

        switch (message) {
            case RegisterRequest request -> authService.handleRegister(request, sender);
            case LoginRequest request -> authService.handleLogin(request, sender);
            case SendChat request -> chatService.handleSendChat(request, sender);
            case TokenLoginRequest request -> sender.send(codec.encode(new ErrorMessage("Not supported yet")));
            case CreateRoomRequest request -> roomService.handleRoomCreation(request, sender);
            case RoomListRequest request -> roomService.handleRoomListRequest(sender);
            case RoomHistoryRequest request -> chatService.handleRoomHistoryRequest(request, sender);
            case Logout request -> authService.handleLogout(sender);
            case OpenDmRequest request -> roomService.handleOpenDm(request, sender);
            case DeleteRoomRequest request -> roomService.handleDeleteRoom(request, sender);
        }
    }
}