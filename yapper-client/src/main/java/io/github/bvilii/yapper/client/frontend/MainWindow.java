package io.github.bvilii.yapper.client.frontend;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import io.github.bvilii.yapper.client.backend.service.auth.AuthObserver;
import io.github.bvilii.yapper.client.backend.service.auth.AuthService;
import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.client.backend.service.chat.ChatService;
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

    private final AuthService authService;

    private JMenuItem logout;

    public MainWindow(AuthService authService, RoomService roomService,
                      ChatService chatService, Session session) {

        this.authService = authService;
        root.add(new AuthScreen(authService), AUTH);
        root.add(new ChatScreen(roomService, chatService, authService, session), CHAT);

        createMenubar();

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
        SwingUtilities.invokeLater(() -> {
            cards.show(root, CHAT);
            logout.setEnabled(true);
        });
    }

    @Override
    public void onRegisterSuccess(String username) {
        SwingUtilities.invokeLater(() -> {
            cards.show(root, CHAT);
            logout.setEnabled(true);
        });
    }

    private void createMenubar() {
        JMenuBar menuBar = new JMenuBar();


        menuBar.add(createSettingsMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());

        frame.setJMenuBar(menuBar);
    }

    private JMenu createViewMenu(){
        JMenu menu = new JMenu("View");

        // --- Theme almenü ---
        JMenu theme = new JMenu("Theme");

        JRadioButtonMenuItem lightTheme = new JRadioButtonMenuItem("Light");
        JRadioButtonMenuItem darkTheme = new JRadioButtonMenuItem("Dark");

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightTheme);
        themeGroup.add(darkTheme);

        if (UIManager.getLookAndFeel() instanceof FlatMacLightLaf) {
            lightTheme.setSelected(true);
        } else {
            darkTheme.setSelected(true);
        }

        lightTheme.addActionListener(e -> setTheme(new FlatMacLightLaf()));
        darkTheme.addActionListener(e -> setTheme(new FlatMacDarkLaf()));

        theme.add(lightTheme);
        theme.add(darkTheme);

        menu.add(theme);
        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JMenu("Help");
        menu.setEnabled(false);

        return menu;
    }

    private JMenu createSettingsMenu() {
        JMenu menu = new JMenu("Settings");

        // --- Log out ---
        logout = new JMenuItem("Log out");
        logout.setEnabled(false);
        logout.addActionListener(e -> logout());

        // --- Exit ---
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        menu.add(logout);
        menu.add(exit);
        return menu;
    }

    private void setTheme(LookAndFeel laf) {
        try {
            UIManager.setLookAndFeel(laf);
            FlatLaf.updateUI();
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Nem sikerült a témaváltás: " + ex.getMessage());
        }
    }

    private void logout() {
        authService.logout();
    }

    @Override
    public void onLoggedOut(){
        SwingUtilities.invokeLater(() -> {
            cards.show(root, AUTH);
            logout.setEnabled(false);
        });
    }
}