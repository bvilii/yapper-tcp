package io.github.bvilii.yapper.client.frontend.auth;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.service.auth.AuthObserver;
import io.github.bvilii.yapper.client.backend.service.auth.AuthService;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel implements AuthObserver {
    private static final String REGISTER_TEXT = "Register";
    private static final String PENDING_TEXT = "Creating account…";

    private final JLabel registerErrorText;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField emailField;
    private final JButton registerButton;

    public RegisterPanel(AuthService authService, Runnable showLogin) {
        // RegisterPanel configurations
        setLayout(new GridBagLayout());

        //create a card shaped background panel for the register form
        JPanel backgroundCard = new JPanel();
        backgroundCard.setLayout(new BoxLayout(backgroundCard, BoxLayout.Y_AXIS));
        backgroundCard.putClientProperty(FlatClientProperties.STYLE, "arc: 24; background: @yapperSurface");
        backgroundCard.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        //fill the card
        JLabel brand = new JLabel("Yapper");
        brand.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        brand.putClientProperty(FlatClientProperties.STYLE, "foreground: @yapperHighlight");
        backgroundCard.add(brand);
        backgroundCard.add(Box.createVerticalStrut(20));


        JLabel title = new JLabel("Create an account");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        backgroundCard.add(title);
        backgroundCard.add(Box.createVerticalStrut(6));

        JLabel textUnderTitle = new JLabel("Then you can yap...");
        textUnderTitle.setForeground(UIManager.getColor("Label.disabledForeground"));
        backgroundCard.add(textUnderTitle);
        backgroundCard.add(Box.createVerticalStrut(20));

        JLabel email = new JLabel("Email");
        email.setFont(email.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(email);
        backgroundCard.add(Box.createVerticalStrut(6));

        emailField = new JTextField(20);
        emailField.setEditable(true);
        emailField.setHorizontalAlignment(SwingConstants.LEFT);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        emailField.putClientProperty("JTextField.showClearButton", true);
        emailField.setMargin(new Insets(9, 12, 9, 12));
        backgroundCard.add(emailField);
        backgroundCard.add(Box.createVerticalStrut(10));

        JLabel username = new JLabel("Username");
        username.setFont(username.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(username);
        backgroundCard.add(Box.createVerticalStrut(6));

        usernameField = new JTextField(20);
        usernameField.setEditable(true);
        usernameField.setHorizontalAlignment(SwingConstants.LEFT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        usernameField.putClientProperty("JTextField.showClearButton", true);
        usernameField.setMargin(new Insets(9, 12, 9, 12));
        backgroundCard.add(usernameField);
        backgroundCard.add(Box.createVerticalStrut(10));

        JLabel password = new JLabel("Password");
        password.setFont(password.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(password);
        backgroundCard.add(Box.createVerticalStrut(6));

        passwordField = new JPasswordField(20);
        passwordField.setEditable(true);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passwordField.setHorizontalAlignment(SwingConstants.LEFT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        passwordField.setEchoChar('*');
        passwordField.setMargin(new Insets(9, 12, 9, 12));
        backgroundCard.add(passwordField);
        backgroundCard.add(Box.createVerticalStrut(20));

        registerButton = new JButton(REGISTER_TEXT);
        registerButton.putClientProperty(FlatClientProperties.STYLE,
                "background: @accentColor; foreground: @yapperOnAccent; "
                        + "disabledBackground: darken(@accentColor,8%); disabledText: fade(@yapperOnAccent,70%)");
        registerButton.setMargin(new Insets(10, 10, 10, 10));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, registerButton.getPreferredSize().height));
        backgroundCard.add(registerButton);
        backgroundCard.add(Box.createVerticalStrut(10));

        registerErrorText = new JLabel();
        registerErrorText.putClientProperty(FlatClientProperties.STYLE, "foreground: @yapperError");
        registerErrorText.setFont(username.getFont().deriveFont(Font.PLAIN));
        registerErrorText.setVisible(false);
        backgroundCard.add(registerErrorText);
        backgroundCard.add(Box.createVerticalStrut(6));

        JLabel account = new JLabel("Already have an account?");
        account.setForeground(UIManager.getColor("Label.disabledForeground"));
        backgroundCard.add(account);
        backgroundCard.add(Box.createVerticalStrut(6));

        JButton login = new JButton("Log in");
        login.setContentAreaFilled(false);
        login.setBorderPainted(false);
        login.setFocusPainted(false);
        login.setBorder(BorderFactory.createEmptyBorder());
        login.putClientProperty(FlatClientProperties.STYLE, "foreground: @yapperLink");
        login.addActionListener(e -> showLogin.run());
        backgroundCard.add(login);

        registerButton.addActionListener(e -> submit(authService));
        emailField.addActionListener(e -> usernameField.requestFocusInWindow());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> registerButton.doClick());

        add(backgroundCard);
    }

    private void submit(AuthService authService) {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.length == 0 || email.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        registerErrorText.setVisible(false);
        setPending(true);
        authService.register(username, new String(password), email);
    }

    @Override
    public void onRegisterSuccess(String username) {
        SwingUtilities.invokeLater(() -> {
            setPending(false);
            passwordField.setText("");
        });
    }

    @Override
    public void onRegisterFailed(String reason) {
        SwingUtilities.invokeLater(() -> {
            setPending(false);
            showError(reason);
        });
    }

    private void setPending(boolean pending) {
        registerButton.setEnabled(!pending);
        registerButton.setText(pending ? PENDING_TEXT : REGISTER_TEXT);
    }

    private void showError(String message) {
        registerErrorText.setText(message);
        registerErrorText.setVisible(true);
    }

    @Override
    public void onLoggedOut(){
        SwingUtilities.invokeLater(() -> {
            emailField.setText("");
            usernameField.setText("");
            passwordField.setText("");
            registerErrorText.setVisible(false);
            setPending(false);
        });
    }
}