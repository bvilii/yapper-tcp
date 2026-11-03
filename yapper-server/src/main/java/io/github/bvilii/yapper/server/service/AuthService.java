package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.RegisterRequest;
import io.github.bvilii.yapper.common.message.server.RegisterFailed;
import io.github.bvilii.yapper.common.message.server.RegisterOk;
import io.github.bvilii.yapper.server.controller.ClientHandler;
import io.github.bvilii.yapper.server.persistence.User;
import io.github.bvilii.yapper.server.persistence.UserAlreadyExistsException;
import io.github.bvilii.yapper.server.persistence.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

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
}
