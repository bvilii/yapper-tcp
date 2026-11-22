package io.github.bvilii.yapper.server;

import com.zaxxer.hikari.HikariDataSource;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.server.config.ServerConfig;
import io.github.bvilii.yapper.server.config.ServerConfigFactory;
import io.github.bvilii.yapper.server.controller.MessageController;
import io.github.bvilii.yapper.server.controller.ServerController;
import io.github.bvilii.yapper.server.network.server.Server;
import io.github.bvilii.yapper.server.persistence.*;
import io.github.bvilii.yapper.server.service.AuthService;
import io.github.bvilii.yapper.server.service.ChatService;
import io.github.bvilii.yapper.server.service.ClientRegistry;
import io.github.bvilii.yapper.server.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        ServerConfig config = ServerConfigFactory.load();
        HikariDataSource dataSource = DataSourceFactory.create(config);
        MessageCodec codec = new MessageCodec();
        ClientRegistry registry = new ClientRegistry();
        UserRepository userRepository = new JdbcUserRepository(dataSource);
        RoomRepository roomRepository = new JdbcRoomRepository(dataSource);
        MessageRepository messageRepository = new JdbcMessageRepository(dataSource);

        AuthService authService = new AuthService(userRepository, codec, registry);
        RoomService roomService = new RoomService(userRepository, roomRepository, registry, codec);
        ChatService chatService = new ChatService(roomRepository, messageRepository, registry, codec);

        MessageController router = new MessageController(codec, chatService, authService, roomService);
        ServerController serverController = new ServerController(new Server(config), router);

        log.info("Starting server on port {}", config.port());
        serverController.startServer();
    }
}
