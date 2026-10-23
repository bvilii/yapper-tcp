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
    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
    private final ServerConfig config;

    public Server(ServerConfig config){
        this.config = config;
    }


    public void start(){
        try (ServerSocket listener = new ServerSocket(config.port())) {
            serverSocket.set(listener);
            log.info("Server started on port: {}", listener.getLocalPort());

            while (!listener.isClosed()) {
                acceptClients(listener);
            }

        } catch (IOException e) {
            if(serverSocket.get() == null){
                log.error("Error while starting or closing the server", e);
            }
        }
    }

    private void acceptClients(ServerSocket listener) {
        try {
            Socket clientSocket = listener.accept();
            log.info("Client connected: {}", clientSocket);
        } catch (IOException e) {
            if(listener.isClosed()){
                return;
            }
            log.warn("Error while accepting a client", e);
        }
    }

    public void shutdown() {
        ServerSocket socket = serverSocket.get();
        if(socket == null){
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
