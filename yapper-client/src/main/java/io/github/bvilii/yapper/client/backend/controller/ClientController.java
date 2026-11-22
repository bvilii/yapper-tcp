package io.github.bvilii.yapper.client.backend.controller;

import io.github.bvilii.yapper.client.backend.network.ServerConnection;

import java.io.IOException;

public class ClientController {
    private final ServerConnection connection;
    private final MessageController router;

    public ClientController(ServerConnection connection, MessageController router) {
        this.connection = connection;
        this.router = router;
    }

    public void start() throws Exception {
        connection.connectToServer();
        connection.startWriterThread();
        startReadingFromServerLoop();
    }

    public void send(String message) {
        connection.writeToServer(message);
    }

    private void startReadingFromServerLoop() throws IOException {
        String line;
        while ((line = connection.readFromServer()) != null) {
            router.handleMessage(line);
        }
    }
}
