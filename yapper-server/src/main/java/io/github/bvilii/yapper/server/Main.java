package io.github.bvilii.yapper.server;


import io.github.bvilii.yapper.server.chat.ChatRoom;
import io.github.bvilii.yapper.server.config.ServerConfig;
import io.github.bvilii.yapper.server.config.ServerConfigFactory;

public class Main {
    public static void main(String[] args) {
        ServerConfig config = ServerConfigFactory.load();

        ChatRoom chatRoom = new ChatRoom();

        Server server = new Server(config, chatRoom);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
