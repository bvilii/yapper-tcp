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

    public MainWindow(AuthService authService, RoomService roomService,
                      ChatService chatService, Session session) {
        root.add(new AuthScreen(authService), AUTH);
        root.add(new ChatScreen(roomService, chatService, session), CHAT);

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
        SwingUtilities.invokeLater(() -> cards.show(root, CHAT));
    }

    @Override
    public void onRegisterSuccess(String username) {
        SwingUtilities.invokeLater(() -> cards.show(root, CHAT));
    }

    private void createMenubar() {
        JMenuBar menuBar = new JMenuBar();


        menuBar.add(createSettingsMenu());
        menuBar.add(createHelpMenu());

        frame.setJMenuBar(menuBar);
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JMenu("Help");


        JMenuItem shortcuts = new JMenuItem("Keyboard shortcuts");

        menu.add(shortcuts);

        return menu;
    }

    private JMenu createSettingsMenu() {
        JMenu menu = new JMenu("Settings");

        // --- Profile ---
        JMenuItem profile = new JMenuItem("Profile");
        profile.addActionListener(e -> {
            // TODO: profil képernyő megnyitása
        });

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

        // --- Log out ---
        JMenuItem logout = new JMenuItem("Log out");
        logout.addActionListener(e -> logout());

        // --- Exit ---
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        menu.add(profile);
        menu.add(theme);
        menu.addSeparator(); // elválasztó vonal
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
        // TODO: ide jön a backend kijelentkeztetés, pl. authService.logout();
        cards.show(root, AUTH);
    }
}