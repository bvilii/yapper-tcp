package io.github.bvilii.yapper.client.frontend.auth;

import io.github.bvilii.yapper.client.backend.service.auth.AuthService;

import javax.swing.*;
import java.awt.*;

public class AuthScreen extends JPanel {
    private final CardLayout cards = new CardLayout();

    public AuthScreen(AuthService service) {
        setLayout(cards);

        LoginPanel login = new LoginPanel(service, () -> cards.show(this, "register"));
        RegisterPanel register = new RegisterPanel(service, () -> cards.show(this, "login"));

        add(login, "login");
        add(register, "register");

        service.addObserver(login);
        service.addObserver(register);
    }

}
