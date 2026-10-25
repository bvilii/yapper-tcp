package io.github.bvilii.yapper.server;

import io.github.bvilii.yapper.server.chat.ChatRoom;
import io.github.bvilii.yapper.server.client.ClientHandler;
import io.github.bvilii.yapper.server.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final Duration ACCEPT_ERROR_DELAY = Duration.ofMillis(500);
    private static final long CLIENT_SHUTDOWN_TIMEOUT_SECONDS = 5;
    private final AtomicReference<ServerSocket> serverSocketRef = new AtomicReference<>();
    private final ServerConfig config;
    private final ChatRoom chatRoom;
    private final ExecutorService clientExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final Set<ClientHandler> connectedClients = ConcurrentHashMap.newKeySet();

    public Server(ServerConfig config, ChatRoom chatRoom) {
        this.config = config;
        this.chatRoom = chatRoom;
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

                startClient(clientSocket);

            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    return;
                }
                log.warn("Error while accepting a client", e);
                waitAfterAcceptError();
            }
        }
    }

    private void waitAfterAcceptError() {
        try {
            Thread.sleep(ACCEPT_ERROR_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void startClient(Socket clientSocket) {
        try {
            new ClientHandler(clientSocket, chatRoom, connectedClients).start(clientExecutor);
        } catch (IOException e) {
            log.warn("Error while opening streams for client: {}", clientSocket, e);
            try {
                clientSocket.close();
            } catch (IOException closeException) {
                log.warn("Error while closing client socket: {}", clientSocket, closeException);
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

        disconnectAllClients();
    }

    private void disconnectAllClients() {
        for (ClientHandler client : connectedClients) {
            client.disconnect();
        }

        clientExecutor.shutdown();
        try {
            if (!clientExecutor.awaitTermination(CLIENT_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("Some client threads did not stop in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}