package io.github.bvilii.yapper.server.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;

public class ClientConnection {
    private final Socket socket;
    private final BufferedWriter bw;
    private final BufferedReader br;
    private SocketWriter writer;
    private SocketReader reader;

    public ClientConnection(Socket socket, BufferedWriter bw, BufferedReader br){
        this.socket = socket;
        this.bw = bw;
        this.br = br;
    }

    public static ClientConnection open(Socket socket) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        return new ClientConnection(socket, bw, br);
    }

    public void startWriter(Executor executor) {
        writer = new SocketWriter(bw);
        executor.execute(writer);
    }

    public void writeToClient(String message){
        writer.addToQueue(message);
    }

    public String getMessageFromClient() throws IOException {
        if(reader == null) {
            reader = new SocketReader(br);
        }

        return reader.readFromClient();
    }
}
