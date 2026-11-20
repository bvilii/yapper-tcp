package io.github.bvilii.yapper.client.frontend.chat;

import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.client.backend.service.chat.ChatService;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel {

    public ChatScreen(RoomService roomService, ChatService chatService, Session session) {
        setLayout(new BorderLayout());

        ChatPanel chat = new ChatPanel(chatService, session);
        chatService.addObserver(chat);

        RoomsPanel rooms = new RoomsPanel(roomService, room -> chat.openRoom(room));
        roomService.addObserver(rooms);

        JSplitPane splitView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rooms, chat);
        add(splitView, BorderLayout.CENTER);
    }
}