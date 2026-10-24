package io.github.bvilii.yapper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final ClientReader reader;
    private final ClientWriter writer;
    private final ChatRoom chatRoom;
    private final Set<ClientHandler> connectedClients;
    private final AtomicBoolean disconnected = new AtomicBoolean(false);


    public ClientHandler(Socket clientSocket, ChatRoom chatRoom, Set<ClientHandler> connectedClients) throws IOException {
        clientSocket.setKeepAlive(true);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

        this.clientSocket = clientSocket;
        this.reader = new ClientReader(socketReader, chatRoom, this);
        this.writer = new ClientWriter(socketWriter, this);
        this.chatRoom = chatRoom;
        this.connectedClients = connectedClients;
    }

    public void start(ExecutorService executor) {
        connectedClients.add(this);
        chatRoom.subscribe(writer);
        executor.execute(reader);
        executor.execute(writer);
    }

    public void disconnect() {
        if (!disconnected.compareAndSet(false, true)) {
            return;
        }

        connectedClients.remove(this);
        chatRoom.unsubscribe(writer);
        writer.stop();
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.warn("Error while closing client socket: {}", clientSocket, e);
        }
        log.info("Client disconnected: {}", clientSocket);
    }

    public boolean isDisconnected() {
        return disconnected.get();
    }

}
