package io.github.bvilii.yapper.server.controller;

import io.github.bvilii.yapper.common.message.InvalidMessageException;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.*;
import io.github.bvilii.yapper.common.message.server.ErrorMessage;
import io.github.bvilii.yapper.server.service.AuthService;
import io.github.bvilii.yapper.server.service.ChatService;

public class MessageController {
    private final MessageCodec codec;
    private final ChatService chatService;
    private final AuthService authService;

    public MessageController(MessageCodec codec, ChatService chatService, AuthService authService) {
        this.codec = codec;
        this.chatService = chatService;
        this.authService = authService;
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
            case LoginRequest request -> sender.send(codec.encode(new ErrorMessage("Not supported yet")));
            case SendChat request -> sender.send(codec.encode(new ErrorMessage("Not supported yet")));
            case TokenLoginRequest request -> sender.send(codec.encode(new ErrorMessage("Not supported yet")));
        }
    }
}
