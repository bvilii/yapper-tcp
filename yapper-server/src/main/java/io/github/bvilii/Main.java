package io.github.bvilii;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(9000)) {
        } catch (IOException e) {
            System.out.println("A problem occurred with the server!");
        }

    }
}
