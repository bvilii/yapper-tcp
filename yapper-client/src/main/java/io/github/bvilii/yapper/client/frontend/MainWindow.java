package io.github.bvilii.yapper.client.frontend;

import io.github.bvilii.yapper.client.backend.service.auth.AuthObserver;
import io.github.bvilii.yapper.client.backend.service.auth.AuthService;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.client.frontend.auth.AuthScreen;
import io.github.bvilii.yapper.client.frontend.chat.ChatScreen;

import javax.swing.*;
import java.awt.*;

public class MainWindow implements AuthObserver {
    private static final String AUTH = "auth";
    private static final String CHAT = "chat";

    private final JFrame frame = new JFrame("Yapper");
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public MainWindow(AuthService authService, RoomService roomService) {
        root.add(new AuthScreen(authService), AUTH);
        root.add(new ChatScreen(roomService), CHAT);

        frame.setContentPane(root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 550);
        frame.setMinimumSize(new Dimension(800, 550));
        frame.setLocationRelativeTo(null);
    }

    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void onLoginSuccess(String username) {
        SwingUtilities.invokeLater(() -> cards.show(root, CHAT));
    }

    @Override
    public void onRegisterSuccess(String username) {
        SwingUtilities.invokeLater(() -> cards.show(root, CHAT));
    }
}