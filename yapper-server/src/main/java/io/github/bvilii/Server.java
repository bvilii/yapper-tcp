package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private volatile boolean running;
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
    private final ServerConfig config;

    public Server(ServerConfig config){
        this.config = config;
    }


    public void start(){
        try (ServerSocket listener = new ServerSocket(config.getPort())) {
            serverSocket.set(listener);
            log.info("Server started on port: {}", listener.getLocalPort());
            running = true;
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            while (running) {
                Socket clientSocket = listener.accept();
                log.info("Client connected: {}", clientSocket);
            }

        } catch (IOException e) {
            if (running) {
                log.error("Unexpected error in server while running", e);
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
