package io.github.bvilii.yapper.client.backend.network;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.Executor;

public class ServerConnection {
    private final Executor executor;
    private BufferedReader br;
    private BufferedWriter bw;
    private SocketWriter writer;
    private SocketReader reader;


    public ServerConnection(Executor executor) {
        this.executor = executor;
    }

    public void connectToServer() throws IOException, GeneralSecurityException {
        Socket serverSocket = connect();

        br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.UTF_8));
        bw = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public void startWriterThread() {
        writer = new SocketWriter(bw);
        executor.execute(writer);
    }

    public void writeToServer(String line){
        writer.addToQueue(line);
    }

    public String readFromServer() throws IOException {
        if(reader == null){
            reader = new SocketReader(br);
        }
        return reader.readLine();
    }

    static SSLSocketFactory createSocketFactory() throws IOException, GeneralSecurityException {
        // 1. Load the certificate: from CHAT_SERVER_CERT if set (self-hosters), else the built-in one
        String certPath = System.getenv("CHAT_SERVER_CERT");
        Certificate cert;
        try (InputStream in = (certPath != null)
                ? new FileInputStream(certPath)
                : ServerConnection.class.getResourceAsStream("/server.crt")) {
            if (in == null) {
                throw new IllegalStateException("server.crt not found on the classpath");
            }
            cert = CertificateFactory.getInstance("X.509").generateCertificate(in);
        }

        // 2. Build a truststore in memory containing ONLY this one certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);                      // start empty
        trustStore.setCertificateEntry("chat-server", cert);

        // 3. TrustManager = the part of TLS that decides "do I trust this server's certificate?"
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // 4. SSLContext: no identity of our own (null), trust only our server
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        return ctx.getSocketFactory();
    }

    private static Socket connect() throws IOException, GeneralSecurityException {
        String host = System.getenv().getOrDefault("CHAT_HOST", "bvilii-yapper.duckdns.org");
        int port = Integer.parseInt(System.getenv().getOrDefault("CHAT_PORT", "9099"));

        SSLSocket socket = (SSLSocket) createSocketFactory().createSocket(host, port);
        try {
            SSLParameters params = socket.getSSLParameters();
            params.setEndpointIdentificationAlgorithm("HTTPS");
            params.setProtocols(new String[] {"TLSv1.3"});
            socket.setSSLParameters(params);

            socket.startHandshake();
            return socket;
        } catch (IOException e) {
            socket.close();
            throw e;
        }
    }
}
