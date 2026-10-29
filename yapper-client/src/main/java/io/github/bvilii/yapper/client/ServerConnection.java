package io.github.bvilii.yapper.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerConnection {
    private final Executor executor;
    private BufferedReader br;
    private BufferedWriter bw;
    private SocketWriter writer;


    public ServerConnection(Executor executor) {
        this.executor = executor;
    }

    public void connect() throws IOException {
        Socket serverSocket = new Socket("localhost", 9099);

        br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.UTF_8));
        bw = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public void startWriter() {
        writer = new SocketWriter(bw);
        executor.execute(writer);
    }

    public void writeToServer(String message){
        writer.addToQueue(message);
    }

    public String getMessageFromServer() throws IOException {
        SocketReader reader = new SocketReader(br);
        return reader.readFromServer();
    }

}
