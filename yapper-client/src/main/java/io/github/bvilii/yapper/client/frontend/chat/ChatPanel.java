package io.github.bvilii.yapper.client.frontend.chat;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.client.backend.service.chat.ChatObserver;
import io.github.bvilii.yapper.client.backend.service.chat.ChatService;
import io.github.bvilii.yapper.common.model.MessageInfo;
import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChatPanel extends JPanel implements ChatObserver {
    private final ChatService chatService;
    private final DefaultListModel<MessageInfo> model = new DefaultListModel<>();
    private final JList<MessageInfo> messageList;
    private final JLabel roomTitle = new JLabel("Select a room");
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton(" ➤ ");

    private RoomInfo currentRoom;

    public ChatPanel(ChatService chatService, Session session) {
        this.chatService = chatService;
        setLayout(new BorderLayout());

        // NORTH: room name
        roomTitle.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");
        roomTitle.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        add(roomTitle, BorderLayout.NORTH);

        // CENTER: message list in a scroll pane
        messageList = new JList<>(model);
        messageList.setCellRenderer(new MessageCellRenderer(session));
        messageList.setFocusable(false);

        JScrollPane scroll = new JScrollPane(messageList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // SOUTH: input field + send button
        inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Write a message...");
        inputField.addActionListener(e -> send());   // Enter
        sendButton.addActionListener(e -> send());   // click

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setInputEnabled(false);   // nothing to send to until a room is opened
    }

    public void openRoom(RoomInfo room) {
        currentRoom = room;
        roomTitle.setText(room.name());
        model.clear();

        chatService.requestRoomHistory(room.id());

        setInputEnabled(true);
        inputField.requestFocusInWindow();
    }

    private void send() {
        if (currentRoom == null) {
            return;
        }
        String text = inputField.getText().strip();
        if (text.isEmpty()) {
            return;
        }
        chatService.sendChat(currentRoom.id(), text);
        inputField.setText("");
    }

    @Override
    public void onMessageArrived(MessageInfo message) {
        SwingUtilities.invokeLater(() -> {
            if (currentRoom == null || message.roomId() != currentRoom.id()) {
                return;
            }
            model.addElement(message);
            messageList.ensureIndexIsVisible(model.size() - 1);
        });
    }

    @Override
    public void onHistoryLoaded(long roomId, List<MessageInfo> messages){
        SwingUtilities.invokeLater(() -> {
            if (currentRoom == null || roomId != currentRoom.id()) {
                return;
            }
            model.addAll(messages);
            messageList.ensureIndexIsVisible(model.size() - 1);
        });
    }



    private void setInputEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
}