package io.github.bvilii.yapper.client.frontend.auth;

import io.github.bvilii.yapper.client.backend.Controller;

import javax.swing.*;
import java.awt.*;

public class AuthScreen extends JPanel {
    private final CardLayout cards = new CardLayout();

    public AuthScreen(Controller controller){
        setLayout(cards);

        LoginPanel login = new LoginPanel(controller, () -> cards.show(this, "register"));
        RegisterPanel register = new RegisterPanel(controller, () -> cards.show(this, "login"));

        add(login, "login");
        add(register, "register");
    }

}
