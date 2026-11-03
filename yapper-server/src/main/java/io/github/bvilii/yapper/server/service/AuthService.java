package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.LoginRequest;
import io.github.bvilii.yapper.common.message.client.RegisterRequest;
import io.github.bvilii.yapper.common.message.server.LoginFailed;
import io.github.bvilii.yapper.common.message.server.LoginOk;
import io.github.bvilii.yapper.common.message.server.RegisterFailed;
import io.github.bvilii.yapper.common.message.server.RegisterOk;
import io.github.bvilii.yapper.server.controller.ClientHandler;
import io.github.bvilii.yapper.server.persistence.User;
import io.github.bvilii.yapper.server.persistence.UserAlreadyExistsException;
import io.github.bvilii.yapper.server.persistence.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private final MessageCodec codec;

    public AuthService(UserRepository userRepository, MessageCodec codec) {
        this.userRepository = userRepository;
        this.codec = codec;
    }

    public void handleRegister(RegisterRequest request, ClientHandler sender) {
        String passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        try {
            User user = userRepository.save(request.username(), request.email(), passwordHash);
            sender.authenticate(user);
            sender.send(codec.encode(new RegisterOk(user.username())));
        } catch (UserAlreadyExistsException e) {
            sender.send(codec.encode(new RegisterFailed("Username or email already taken")));
        }
    }

    public void handleLogin(LoginRequest request, ClientHandler sender) {
        Optional<User> found = userRepository.findByUsername(request.username());

        if (found.isEmpty() || !BCrypt.checkpw(request.password(), found.get().passwordHash())) {
            sender.send(codec.encode(new LoginFailed("Invalid username or password")));
            return;
        }

        User user = found.get();
        sender.authenticate(user);
        sender.send(codec.encode(new LoginOk(user.username(), null)));
    }
}
