package io.github.bvilii.yapper.client;

import java.io.IOException;

public class Controller implements Runnable {
    private final ServerConnection connection;

    public Controller(ServerConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            connection.connect();
            connection.startWriter();

            startReadingFromServerLoop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String message) {
        connection.writeToServer(message);
    }

    private void startReadingFromServerLoop() throws IOException {
        while (true) {
            String message = connection.getMessageFromServer();
            System.out.println(message);
        }
    }
}
