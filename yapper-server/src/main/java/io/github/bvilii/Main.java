package io.github.bvilii;


import io.github.bvilii.config.ServerConfig;
import io.github.bvilii.config.ServerConfigFactory;

public class Main {
    public static void main(String[] args) {
        ServerConfig config = ServerConfigFactory.load();

        Server server = new Server(config);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
