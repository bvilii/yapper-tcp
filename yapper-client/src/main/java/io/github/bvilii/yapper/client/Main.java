package io.github.bvilii.yapper.client;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import io.github.bvilii.yapper.client.backend.controller.ClientController;
import io.github.bvilii.yapper.client.backend.controller.MessageController;
import io.github.bvilii.yapper.client.backend.network.ServerConnection;
import io.github.bvilii.yapper.client.backend.service.auth.AuthService;
import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.client.backend.service.chat.ChatService;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.client.frontend.MainWindow;
import io.github.bvilii.yapper.common.message.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();


        MessageCodec codec = new MessageCodec();
        Session session = new Session();
        ServerConnection connection = new ServerConnection(executor);
        AuthService authService = new AuthService(connection, codec, session);
        RoomService roomService = new RoomService(codec, connection);
        authService.addObserver(roomService);
        ChatService chatService = new ChatService(codec, connection);
        MessageController router = new MessageController(codec, authService, roomService, chatService);
        ClientController controller = new ClientController(connection, router);


        SwingUtilities.invokeLater(() -> {

            FlatInterFont.install();
            FlatLaf.setPreferredFontFamily(FlatInterFont.FAMILY);
            FlatLaf.registerCustomDefaultsSource("io.github.bvilii.yapper.client.themes");
            FlatLaf.setup(new FlatMacDarkLaf() {
                @Override
                public void provideErrorFeedback(Component component) {
                    // NO BEEP
                }
            });
            UIManager.put("Component.focusWidth", 0);

            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("CheckBox.arc", 12);
            UIManager.put("List.selectionArc", 12);
            UIManager.put("List.selectionInsets", new Insets(2, 6, 2, 6));

            MainWindow window = new MainWindow(authService, roomService, chatService, session);
            authService.addObserver(window);
            window.show();
        });

        try {
            controller.start();
        } catch (IOException | GeneralSecurityException e) {
            log.error("Could not connect to the server", e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Could not connect to the server.",
                        "Yapper", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }

    }
}