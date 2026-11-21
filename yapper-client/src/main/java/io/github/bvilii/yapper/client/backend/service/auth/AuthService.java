package io.github.bvilii.yapper.client.backend.service.auth;

import io.github.bvilii.yapper.client.backend.network.ServerConnection;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.LoginRequest;
import io.github.bvilii.yapper.common.message.client.Logout;
import io.github.bvilii.yapper.common.message.client.RegisterRequest;
import io.github.bvilii.yapper.common.message.server.LoginFailed;
import io.github.bvilii.yapper.common.message.server.LoginOk;
import io.github.bvilii.yapper.common.message.server.RegisterFailed;
import io.github.bvilii.yapper.common.message.server.RegisterOk;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuthService implements AuthSubject {
    private final List<AuthObserver> observers = new CopyOnWriteArrayList<>();
    private final ServerConnection connection;
    private final MessageCodec codec;
    private final Session session;

    public AuthService(ServerConnection connection, MessageCodec codec, Session session) {
        this.connection = connection;
        this.codec = codec;
        this.session = session;
    }

    @Override
    public void addObserver(AuthObserver observer) {
        observers.add(observer);
    }

    public void handleRegisterSuccess(RegisterOk message) {
        session.start(message.username());
        for (AuthObserver observer : observers) {
            observer.onRegisterSuccess(message.username());
        }
    }

    public void handleRegisterFailure(RegisterFailed message) {
        for (AuthObserver observer : observers) {
            observer.onRegisterFailed(message.reason());
        }
    }

    public void handleLoginFailure(LoginFailed message) {
        for (AuthObserver observer : observers) {
            observer.onLoginFailed(message.reason());
        }
    }

    public void handleLoginSuccess(LoginOk message) {
        session.start(message.username());
        for (AuthObserver observer : observers) {
            observer.onLoginSuccess(message.username());
        }
    }

    public void login(String username, String password) {
        connection.writeToServer(codec.encode(new LoginRequest(username, password)));
    }

    public void register(String username, String password, String email) {
        connection.writeToServer(codec.encode(new RegisterRequest(username, password, email)));
    }

    public void logout() {
        connection.writeToServer(codec.encode(new Logout()));
        session.clear();
        for (AuthObserver observer : observers) {
            observer.onLoggedOut();
        }
    }
}