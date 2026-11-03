package io.github.bvilii.yapper.server.network;

import io.github.bvilii.yapper.server.config.ServerConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerConfig config;
    private ServerSocket serverSocket;

    public Server(ServerConfig config) {
        this.config = config;
    }

    public void open() throws IOException {
        serverSocket = new ServerSocket(config.port());
    }

    public ClientConnection acceptClient() throws IOException {
        Socket socket = serverSocket.accept();
        return ClientConnection.open(socket);
    }
}
