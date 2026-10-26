package io.github.bvilii.yapper.server.client;

import io.github.bvilii.yapper.server.chat.Observer;
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
    private final Disconnectable connection;

    public ClientWriter(BufferedWriter socketWriter, Disconnectable connection) {
        this.socketWriter = socketWriter;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = messagesQueue.take();
                if (connection.isDisconnected()) {
                    return;
                }

                socketWriter.write(msg);
                socketWriter.write('\n');
                socketWriter.flush();
            }
        } catch (IOException e) {
            if (!connection.isDisconnected()) {
                log.warn("Error while writing to client", e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            connection.disconnect();
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
