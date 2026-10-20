package io.github.bvilii;


import io.github.bvilii.config.EnvConfigLoader;
import io.github.bvilii.config.ServerConfig;

public class Main {
    public static void main(String[] args) {
        ServerConfig config = EnvConfigLoader.load();

        Server server = new Server(config);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
