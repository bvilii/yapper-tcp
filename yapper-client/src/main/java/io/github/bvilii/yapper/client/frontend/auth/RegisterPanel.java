package io.github.bvilii.yapper.client.frontend.auth;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(RegisterPanel.class);

    public RegisterPanel(Controller controller, Runnable showLogin){
        // RegisterPanel configurations
        setLayout(new GridBagLayout());

        //create a card shaped background panel for the register form
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


        JLabel title = new JLabel("Create an account");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        title.setForeground(Color.WHITE);
        backgroundCard.add(title);
        backgroundCard.add(Box.createVerticalStrut(6));

        JLabel textUnderTitle = new JLabel("Then you can yap...");
        textUnderTitle.setForeground(UIManager.getColor("Label.disabledForeground"));
        backgroundCard.add(textUnderTitle);
        backgroundCard.add(Box.createVerticalStrut(20));

        JLabel email = new JLabel("Email");
        email.setForeground(Color.WHITE);
        email.setFont(email.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(email);
        backgroundCard.add(Box.createVerticalStrut(6));

        JTextField emailField = new JTextField(20);
        emailField.setEditable(true);
        emailField.putClientProperty(FlatClientProperties.STYLE, "focusedBorderColor: #ab0a6b");
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setHorizontalAlignment(JTextField.LEFT);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        emailField.putClientProperty("JTextField.showClearButton", true);
        emailField.setMargin(new Insets(9, 12, 9, 12));
        backgroundCard.add(emailField);
        backgroundCard.add(Box.createVerticalStrut(10));

        JLabel username = new JLabel("Username");
        username.setForeground(Color.WHITE);
        username.setFont(username.getFont().deriveFont(Font.BOLD));
        backgroundCard.add(username);
        backgroundCard.add(Box.createVerticalStrut(6));

        JTextField usernameField = new JTextField(20);
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

        JPasswordField passwordField = new JPasswordField(20);
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

        JButton registerButton = new JButton("Register");
        registerButton.setMargin(new Insets(10, 10, 10, 10));
        registerButton.setForeground(Color.WHITE);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        registerButton.setBackground(appThemeCoolor);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, registerButton.getPreferredSize().height));
        backgroundCard.add(registerButton);
        backgroundCard.add(Box.createVerticalStrut(10));

        JLabel account = new JLabel("Already have an account?");
        account.setForeground(UIManager.getColor("Label.disabledForeground"));
        backgroundCard.add(account);
        backgroundCard.add(Box.createVerticalStrut(6));

        JButton login = new JButton("Log in");
        login.setContentAreaFilled(false);
        login.setBorderPainted(false);
        login.setFocusPainted(false);
        login.setBorder(BorderFactory.createEmptyBorder());
        login.setForeground(appThemeCoolor);
        login.addActionListener(e -> showLogin.run());
        backgroundCard.add(login);

        add(backgroundCard);
    }
}
