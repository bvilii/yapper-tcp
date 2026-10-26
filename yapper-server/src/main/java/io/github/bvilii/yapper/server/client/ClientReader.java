package io.github.bvilii.yapper.server.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientReader implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientReader.class);
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private final BufferedReader socketReader;
    private final MessageReceiver messageReceiver;
    private final Disconnectable connection;

    public ClientReader(BufferedReader socketReader, MessageReceiver messageReceiver, Disconnectable connection) {
        this.socketReader = socketReader;
        this.messageReceiver = messageReceiver;
        this.connection = connection;
    }


    @Override
    public void run() {
        try {
            String line;
            while ((line = readLine()) != null) {
                messageReceiver.onMessageReceived(line);
            }
        } catch (IOException e) {
            if (!connection.isDisconnected()) {
                log.warn("Error while reading from client", e);
            }
        } finally {
            connection.disconnect();
        }
    }

    private String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        int character;
        while ((character = socketReader.read()) != -1) {
            if (character == '\n') {
                return line.toString();
            }
            if (character != '\r') {
                line.append((char) character);
            }
            if (line.length() > MAX_MESSAGE_LENGTH) {
                throw new IOException("Message is longer than " + MAX_MESSAGE_LENGTH + " characters");
            }
        }
        return null;
    }
}
