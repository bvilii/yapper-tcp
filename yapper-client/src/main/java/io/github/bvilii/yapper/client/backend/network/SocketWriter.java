package io.github.bvilii.yapper.client.backend.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketWriter implements Runnable {
    private final BlockingQueue<String> messageQueue;
    private final BufferedWriter bw;

    public SocketWriter(BufferedWriter bw) {
        messageQueue = new LinkedBlockingQueue<>();
        this.bw = bw;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String line = messageQueue.take();
                send(line);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void send(String line) {
        try {
            bw.write(line);
            bw.write('\n');
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(String line) {
        messageQueue.add(line);
    }
}
