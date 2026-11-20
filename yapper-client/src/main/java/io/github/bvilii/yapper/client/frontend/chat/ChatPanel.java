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
    static final int HEADER_HEIGHT = 64;

    private final ChatService chatService;
    private final DefaultListModel<MessageInfo> model = new DefaultListModel<>();
    private final JList<MessageInfo> messageList;
    private final JLabel roomTitle = new JLabel("Select a room");
    private final JLabel roomMembers = new JLabel(" ");
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton(" ➤ ");

    private RoomInfo currentRoom;

    public ChatPanel(ChatService chatService, Session session) {
        this.chatService = chatService;
        setLayout(new BorderLayout());

        // NORTH: room name + members, with a line under it
        roomTitle.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        roomMembers.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
        roomMembers.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(roomTitle);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(roomMembers);

        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        left.weightx = 1;
        header.add(titleBlock, left);

        JPanel headerWithLine = new JPanel(new BorderLayout());
        headerWithLine.setOpaque(false);
        headerWithLine.add(header, BorderLayout.CENTER);
        headerWithLine.add(new JSeparator(), BorderLayout.SOUTH);
        add(headerWithLine, BorderLayout.NORTH);

        // CENTER: message list in a scroll pane
        messageList = new JList<>(model);
        messageList.setCellRenderer(new MessageCellRenderer(session));
        messageList.setFocusable(false);
        messageList.putClientProperty(FlatClientProperties.STYLE, "background: @background");
        messageList.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane scroll = new JScrollPane(messageList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // SOUTH: input field + send button
        inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "yappa yappa...");
        inputField.addActionListener(e -> send());   // Enter
        sendButton.addActionListener(e -> send());   // click

        inputField.setMargin(new Insets(8, 12, 8, 12));
        sendButton.putClientProperty(FlatClientProperties.STYLE,
                "background: @accentColor; foreground: @yapperOnAccent; "
                        + "disabledBackground: fade(@accentColor,40%); disabledText: fade(@yapperOnAccent,70%)");

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel inputWithLine = new JPanel(new BorderLayout());
        inputWithLine.setOpaque(false);
        inputWithLine.add(new JSeparator(), BorderLayout.NORTH);
        inputWithLine.add(inputPanel, BorderLayout.CENTER);
        add(inputWithLine, BorderLayout.SOUTH);

        setInputEnabled(false);   // nothing to send to until a room is opened
    }

    public void openRoom(RoomInfo room) {
        currentRoom = room;
        roomTitle.setText(room.name());
        roomMembers.setText(String.join(", ", room.members()));
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