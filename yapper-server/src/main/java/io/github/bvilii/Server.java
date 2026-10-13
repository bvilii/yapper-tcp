package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private volatile boolean running;
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final List<Socket> registry = new ArrayList<>();
    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();


    public void start(){

        Properties properties = new Properties();

        try (InputStream input = Server.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null){
                log.warn("Unable to find config.properties in resources");
            }else {
                properties.load(input);
            }
        } catch (Exception e) {
            log.warn("Error reading config.properties", e);
        }

        int port = Integer.parseInt(properties.getProperty("server.port", "9099"));


        try (ServerSocket listener = new ServerSocket(port)) {
            serverSocket.set(listener);
            log.info("Server started on port: {}", listener.getLocalPort());
            running = true;
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            while (running) {
                Socket clientSocket = listener.accept();
                registry.add(clientSocket);
                log.info("Client connected: {}", listener);
            }

        } catch (IOException e) {
            if (running) {
                log.error("Unexpected error in server while running", e);
                running = false;
            }
        }
    }

    public void shutdown() {
        if (!shuttingDown.compareAndSet(false, true)) {
            return;
        }
        log.info("Server shutting down...");
        running = false;

        try {
            ServerSocket socket = serverSocket.get();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.warn("Error while closing the server", e);
        }
    }


}
