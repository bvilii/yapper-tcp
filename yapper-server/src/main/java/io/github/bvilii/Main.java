package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            log.info("The server has started on port: {}", serverSocket.getLocalPort());
        } catch (IOException e) {
            log.error("An unexpected error occurred while running the server", e);
        }

    }
}
