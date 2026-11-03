package io.github.bvilii.yapper.server.controller;

import io.github.bvilii.yapper.server.network.ClientConnection;
import io.github.bvilii.yapper.server.network.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerController {
    private static final Logger log = LoggerFactory.getLogger(ServerController.class);
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Server server;
    private final MessageController router;

    public ServerController(Server server, MessageController router) {
        this.server = server;
        this.router = router;
    }

    public void startServer() throws IOException {
        server.open();
        acceptClientLoop();
    }

    private void acceptClientLoop() {
        while (true) {
            try {
                ClientConnection connection = server.acceptClient();

                ClientHandler handler = new ClientHandler(executor, connection, router);
                executor.execute(handler);

            } catch (IOException e) {
                log.warn("Error while accepting a client", e);
            }
        }
    }

}
