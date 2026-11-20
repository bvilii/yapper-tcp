package io.github.bvilii.yapper.client.frontend.auth;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.service.auth.AuthObserver;
import io.github.bvilii.yapper.client.backend.service.auth.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel implements AuthObserver {
    private static final String LOGIN_TEXT = "Log in";
    private static final String PENDING_TEXT = "Logging in…";

    private final JLabel loginErrorText;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginPanel(AuthService authService, Runnable showRegister) {
        // LoginPanel configurations
        setLayout(new GridBagLayout());

        //create a card shaped background panel for the login form
        JPanel backgroundCard = new JPanel();
        backgroundCard.setLayout(new BoxLayout(backgroundCard, BoxLayout.Y_AXIS));
        backgroundCard.putClientProperty(FlatClientProperties.STYLE, "arc: 24; background: lighten(@background,5%)");
        backgroundCard.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        //fill the card
        Color appThemeCoolor = new Color(0xab0a6b);

        JLabel brand = new JLabel("Yapper");
        brand.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        brand.setForeground(appThemeCoolor);
        backgroundCard.add(brand);
        backgroundCard.add(Box.createVerticalStrut(20));


        JLabel greeting = new JLabel("Welcome back");
        greeting.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        greeting.setForeground(Color.WHITE);
        backgroundCard.add(greeting);
        backgroundCard.add(Box.createVerticalStrut(6));

        JLabel textUnderGreeting = new JLabel("Log in to continue yapping");
        textUnderGreeting.setForeground(UIManager.getColor("Label.disabledForeground"));
        backgroundCard.add(textUnderGreeting);
        backgroundCard.add(Box.createVerticalStrut(20));

        JLabel username = new JLabel("Username");
        username.setForeground(Color.WHITE);
        username.setFont(username.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(username);
        backgroundCard.add(Box.createVerticalStrut(6));

        usernameField = new JTextField(20);
        usernameField.setEditable(true);
        usernameField.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #ab0a6b");
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setHorizontalAlignment(JTextField.LEFT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        usernameField.putClientProperty("JTextField.showClearButton", true);
        usernameField.setMargin(new Insets(9, 12, 9, 12));
        backgroundCard.add(usernameField);
        backgroundCard.add(Box.createVerticalStrut(10));

        JLabel password = new JLabel("Password");
        password.setForeground(Color.WHITE);
        password.setFont(password.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(password);
        backgroundCard.add(Box.createVerticalStrut(6));

        passwordField = new JPasswordField(20);
        passwordField.setEditable(true);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #ab0a6b; showRevealButton: true");
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setHorizontalAlignment(JTextField.LEFT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        passwordField.setEchoChar('*');
        passwordField.setMargin(new Insets(9, 12, 9, 12));
        backgroundCard.add(passwordField);
        backgroundCard.add(Box.createVerticalStrut(20));

        loginButton = new JButton(LOGIN_TEXT);
        loginButton.putClientProperty(FlatClientProperties.STYLE,
                "disabledBackground: darken(#ab0a6b,12%); disabledText: #e0e0e0");
        loginButton.setMargin(new Insets(10, 10, 10, 10));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        loginButton.setBackground(appThemeCoolor);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginButton.getPreferredSize().height));
        backgroundCard.add(loginButton);
        backgroundCard.add(Box.createVerticalStrut(10));

        loginErrorText = new JLabel();
        loginErrorText.setForeground(Color.RED);
        loginErrorText.setFont(username.getFont().deriveFont(Font.PLAIN));
        loginErrorText.setVisible(false);
        backgroundCard.add(loginErrorText);
        backgroundCard.add(Box.createVerticalStrut(6));

        JLabel account = new JLabel("Need an account?");
        account.setForeground(UIManager.getColor("Label.disabledForeground"));
        backgroundCard.add(account);
        backgroundCard.add(Box.createVerticalStrut(6));

        JButton register = new JButton("Register");
        register.setContentAreaFilled(false);
        register.setBorderPainted(false);
        register.setFocusPainted(false);
        register.setBorder(BorderFactory.createEmptyBorder());
        register.setForeground(appThemeCoolor);
        register.addActionListener(e -> showRegister.run());
        backgroundCard.add(register);

        loginButton.addActionListener(e -> submit(authService));

        add(backgroundCard);
    }

    private void submit(AuthService authService) {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            showError("Please fill in all fields");
            return;
        }

        loginErrorText.setVisible(false);
        setPending(true);
        authService.login(username, new String(password));
    }

    @Override
    public void onLoginSuccess(String username) {
        SwingUtilities.invokeLater(() -> {
            setPending(false);
            passwordField.setText("");
        });
    }

    @Override
    public void onLoginFailed(String reason) {
        SwingUtilities.invokeLater(() -> {
            setPending(false);
            passwordField.setText("");
            showError(reason);
            passwordField.requestFocusInWindow();
        });
    }

    private void setPending(boolean pending) {
        loginButton.setEnabled(!pending);
        loginButton.setText(pending ? PENDING_TEXT : LOGIN_TEXT);
    }

    private void showError(String message) {
        loginErrorText.setText(message);
        loginErrorText.setVisible(true);
    }
}