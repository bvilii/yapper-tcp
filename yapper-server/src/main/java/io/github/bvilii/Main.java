package io.github.bvilii;


public class Main {
    public static void main(String[] args) {
        ServerConfig config = EnvConfigLoader.load();

        Server server = new Server(config);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.start();
    }
}
