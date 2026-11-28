package io.github.bvilii.yapper.server.network.server;

import io.github.bvilii.yapper.server.config.ServerConfig;
import io.github.bvilii.yapper.server.network.client.ClientConnection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class Server {
    private final ServerConfig config;
    private ServerSocket serverSocket;

    public Server(ServerConfig config) {
        this.config = config;
    }

    public void open() throws IOException, GeneralSecurityException {
        serverSocket = createServerSocket(config.port());
    }

    public ClientConnection acceptClient() throws IOException {
        Socket socket = serverSocket.accept();
        return ClientConnection.open(socket);
    }

    static SSLServerSocket createServerSocket(int port) throws IOException, GeneralSecurityException {
        // 1. Where is the keystore, and what's its password? (from environment variables)
        String keystorePath = System.getenv().getOrDefault("YAPPER_KEYSTORE", "server.p12");
        String pass = System.getenv("YAPPER_KEYSTORE_PASSWORD");
        if (pass == null) {
            throw new IllegalStateException("YAPPER_KEYSTORE_PASSWORD is not set");
        }
        char[] password = pass.toCharArray();

        // 2. Open server.p12 -> now we have the private key + certificate in memory
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = new FileInputStream(keystorePath)) {
            keyStore.load(in, password);
        }

        // 3. KeyManager = the part of TLS that USES the private key:
        //    sends the certificate and makes the handshake signature
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password);

        // 4. SSLContext = a bundle of TLS settings: "use this key manager"
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, null);

        // 5. Make the server socket from that context, allow only TLS 1.3
        SSLServerSocket server = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(port);
        server.setEnabledProtocols(new String[]{"TLSv1.3"});
        return server;
    }
}
