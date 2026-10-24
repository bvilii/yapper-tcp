package io.github.bvilii.yapper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientWriter implements Runnable, Observer {
    private static final Logger log = LoggerFactory.getLogger(ClientWriter.class);
    private static final String POISON_PILL = "POISON_PILL";
    private final BufferedWriter socketWriter;
    private final BlockingQueue<String> messagesQueue = new LinkedBlockingQueue<>();
    private final ClientHandler handler;

    public ClientWriter(BufferedWriter socketWriter, ClientHandler handler) {
        this.socketWriter = socketWriter;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = messagesQueue.take();
                if (handler.isDisconnected()) {
                    return;
                }

                socketWriter.write(msg);
                socketWriter.write('\n');
                socketWriter.flush();
            }
        } catch (IOException e) {
            if (!handler.isDisconnected()) {
                log.warn("Error while writing to client", e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            handler.disconnect();
        }
    }

    @Override
    public void update(String msg) {
        messagesQueue.offer(msg);
    }

    public void stop() {
        messagesQueue.offer(POISON_PILL);
    }
}
