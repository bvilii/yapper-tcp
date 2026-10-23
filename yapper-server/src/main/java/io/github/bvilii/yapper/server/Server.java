package io.github.bvilii.yapper.server;

import io.github.bvilii.yapper.server.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final AtomicReference<ServerSocket> serverSocketRef = new AtomicReference<>();
    private final ServerConfig config;

    public Server(ServerConfig config) {
        this.config = config;
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(config.port())) {
            serverSocketRef.set(serverSocket);
            log.info("Server started on port: {}", serverSocket.getLocalPort());

            acceptClientsLoop(serverSocket);

        } catch (IOException e) {
            if (serverSocketRef.get() == null) {
                log.error("Error while starting", e);
            } else {
                log.error("Error while closing the server", e);
            }
        }
    }

    private void acceptClientsLoop(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {

                Socket clientSocket = serverSocket.accept();
                log.info("Client connected: {}", clientSocket);

            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    return;
                }
                log.warn("Error while accepting a client", e);
            }
        }
    }

    public void shutdown() {
        ServerSocket socket = serverSocketRef.get();
        if (socket == null) {
            return;
        }

        log.info("Server shutting down...");
        try {
            socket.close();
        } catch (IOException e) {
            log.warn("Error while closing the server", e);
        }
    }

}
