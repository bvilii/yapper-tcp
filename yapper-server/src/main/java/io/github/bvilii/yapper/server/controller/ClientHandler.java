package io.github.bvilii.yapper.server.controller;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.server.network.ClientConnection;

import java.io.IOException;
import java.util.concurrent.Executor;

public class ClientHandler implements Runnable {
    private final Executor executor;
    private final ClientConnection connection;
    private final MessageController router;

    public ClientHandler(Executor executor, ClientConnection connection, MessageController router) {
        this.executor = executor;
        this.connection = connection;
        this.router = router;
    }

    @Override
    public void run() {
        connection.startWriter(executor);

        try {
            startReadingFromClientLoop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startReadingFromClientLoop() throws IOException {
        String line;
        while ((line = connection.getMessageFromClient()) != null) {
            router.handleMessage(line, this);
        }
    }


    public void send(String message) {
        connection.writeToClient(message);
    }
}
