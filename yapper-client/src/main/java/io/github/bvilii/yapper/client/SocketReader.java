package io.github.bvilii.yapper.client;

import java.io.BufferedReader;
import java.io.IOException;

public class SocketReader {
    private final BufferedReader br;

    public SocketReader(BufferedReader br) {
        this.br = br;
    }

    public String readFromServer() throws IOException {
        String line = br.readLine();
        if (line == null){
            System.exit(1);
        }

        return line;
    }
}
