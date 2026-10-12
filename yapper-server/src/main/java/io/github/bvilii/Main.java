    package io.github.bvilii;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.io.IOException;
    import java.net.ServerSocket;
    import java.util.concurrent.atomic.AtomicBoolean;

    public class Main {
        private static final Logger log = LoggerFactory.getLogger(Main.class);
        private static volatile boolean running;
        private static final AtomicBoolean shuttingDown = new AtomicBoolean(false);

        public static void main(String[] args) {

            try (ServerSocket serverSocket = new ServerSocket(9000)) {
                log.info("The server has started on port: {}", serverSocket.getLocalPort());
                running = true;

                Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(serverSocket)));

            } catch (IOException e) {
                log.error("An unexpected error occurred while running the server", e);
            }

        }

        public static void shutdown(ServerSocket serverSocket){
            if (!shuttingDown.compareAndSet(false, true)){
                return;
            }
            log.info("Server shutting down...");
            running = false;

            try {
                serverSocket.close();
            } catch (IOException e) {
                log.warn("Error while closing the server", e);
            }
        }
    }
