package io.github.bvilii.yapper.client.frontend.chat;

import io.github.bvilii.yapper.client.backend.service.auth.AuthService;
import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.client.backend.service.chat.ChatService;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel {

    public ChatScreen(RoomService roomService, ChatService chatService, AuthService authService, Session session) {
        setLayout(new BorderLayout());

        ChatPanel chat = new ChatPanel(chatService, session);
        chatService.addObserver(chat);
        authService.addObserver(chat);

        RoomsPanel rooms = new RoomsPanel(roomService, session, room -> chat.openRoom(room));
        roomService.addObserver(rooms);
        authService.addObserver(rooms);

        JSplitPane splitView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rooms, chat);
        add(splitView, BorderLayout.CENTER);
    }
}