package io.github.bvilii.yapper.client;

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
        while(true){
            try {
                String message = messageQueue.take();

                sendToServer(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);

            }
        }
    }

    private void sendToServer(String message) {
        try {
            bw.write(message);
            bw.write('\n');
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(String message){
        messageQueue.offer(message);
    }
}
