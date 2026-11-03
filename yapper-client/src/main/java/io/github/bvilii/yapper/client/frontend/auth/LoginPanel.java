package io.github.bvilii.yapper.client.frontend.auth;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.Controller;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(Controller controller, Runnable showRegister){
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

        JButton loginButton = new JButton("Log in");
        loginButton.setMargin(new Insets(10, 10, 10, 10));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        loginButton.setBackground(appThemeCoolor);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginButton.getPreferredSize().height));
        backgroundCard.add(loginButton);
        backgroundCard.add(Box.createVerticalStrut(10));

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



        add(backgroundCard);
    }
}
