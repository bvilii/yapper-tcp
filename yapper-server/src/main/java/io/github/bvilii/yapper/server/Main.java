package io.github.bvilii.yapper.server;

import com.zaxxer.hikari.HikariDataSource;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.server.config.ServerConfig;
import io.github.bvilii.yapper.server.config.ServerConfigFactory;
import io.github.bvilii.yapper.server.controller.MessageController;
import io.github.bvilii.yapper.server.controller.ServerController;
import io.github.bvilii.yapper.server.network.Server;
import io.github.bvilii.yapper.server.persistence.DataSourceFactory;
import io.github.bvilii.yapper.server.persistence.JdbcUserRepository;
import io.github.bvilii.yapper.server.persistence.UserRepository;
import io.github.bvilii.yapper.server.service.AuthService;
import io.github.bvilii.yapper.server.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        ServerConfig config = ServerConfigFactory.load();
        HikariDataSource dataSource = DataSourceFactory.create(config);
        MessageCodec codec = new MessageCodec();

        UserRepository userRepository = new JdbcUserRepository(dataSource);
        AuthService authService = new AuthService(userRepository, codec);
        ChatService chatService = new ChatService();

        MessageController router = new MessageController(codec, chatService, authService);
        ServerController serverController = new ServerController(new Server(config), router);

        log.info("Starting server on port {}", config.port());
        serverController.startServer();
    }
}
