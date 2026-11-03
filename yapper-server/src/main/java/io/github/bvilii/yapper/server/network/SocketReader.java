package io.github.bvilii.yapper.server.network;

import java.io.BufferedReader;
import java.io.IOException;

public class SocketReader {
    private final BufferedReader br;

    public SocketReader(BufferedReader br) {
        this.br = br;
    }

    public String readFromClient() throws IOException {
        String line = br.readLine();
        return line;
    }
}
