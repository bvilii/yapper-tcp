package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private volatile boolean running;
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final List<Socket> registry = new ArrayList<>();
    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();


    public void start(){
        try (ServerSocket listener = new ServerSocket(9000)) {
            serverSocket.set(listener);
            log.info("The server has started on port: {}", listener.getLocalPort());
            running = true;
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            while (running) {
                Socket clientSocket = listener.accept();
                registry.add(clientSocket);
                log.info("A client has connected: {}", clientSocket);
            }

        } catch (IOException e) {
            if (running) {
                log.error("An unexpected error occurred while running the server", e);
                running = false;
            }
        }
    }

    public void shutdown() {
        if (!shuttingDown.compareAndSet(false, true)) {
            return;
        }
        log.info("Server shutting down...");
        running = false;

        try {
            ServerSocket socket = serverSocket.get();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.warn("Error while closing the server", e);
        }
    }


}
