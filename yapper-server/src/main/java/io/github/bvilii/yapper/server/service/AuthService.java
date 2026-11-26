package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.LoginRequest;
import io.github.bvilii.yapper.common.message.client.RegisterRequest;
import io.github.bvilii.yapper.common.message.server.*;
import io.github.bvilii.yapper.server.controller.ClientHandler;
import io.github.bvilii.yapper.server.persistence.User;
import io.github.bvilii.yapper.server.persistence.UserAlreadyExistsException;
import io.github.bvilii.yapper.server.persistence.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private final MessageCodec codec;
    private final ClientRegistry registry;

    public AuthService(UserRepository userRepository, MessageCodec codec, ClientRegistry registry) {
        this.userRepository = userRepository;
        this.codec = codec;
        this.registry = registry;
    }

    public void handleRegister(RegisterRequest request, ClientHandler sender) {
        if (sender.isAuthenticated()) {
            sender.send(codec.encode(new ErrorMessage("Already logged in")));
            return;
        }

        String passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        try {
            User user = userRepository.save(request.username(), request.email(), passwordHash);
            sender.authenticate(user);
            registerClientToOnline(sender, user);
            sender.send(codec.encode(new RegisterOk(user.username())));
        } catch (UserAlreadyExistsException e) {
            sender.send(codec.encode(new RegisterFailed("Username or email already taken")));
        }
    }

    public void handleLogin(LoginRequest request, ClientHandler sender) {
        if (sender.isAuthenticated()) {
            sender.send(codec.encode(new ErrorMessage("Already logged in")));
            return;
        }

        Optional<User> found = userRepository.findByUsername(request.username());

        if (found.isEmpty() || !BCrypt.checkpw(request.password(), found.get().passwordHash())) {
            sender.send(codec.encode(new LoginFailed("Invalid username or password")));
            return;
        }

        User user = found.get();
        sender.authenticate(user);
        registerClientToOnline(sender, user);
        sender.send(codec.encode(new LoginOk(user.username())));
    }

    private void registerClientToOnline(ClientHandler handler, User user) {
        registry.register(user.id(), handler)
                .ifPresent(previous -> {
                    previous.send(codec.encode(new ErrorMessage("Logged in from another device")));
                    previous.logout();
                });
    }

    public void handleLogout(ClientHandler sender) {
        User user = sender.getUser();
        if (user == null) {
            return;
        }
        registry.unregister(user.id(), sender);
        sender.logout();
    }
}
