package io.github.bvilii.yapper.client;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();

        ServerConnection connection = new ServerConnection(executor);
        Controller controller = new Controller(connection);
        executor.execute(controller);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            controller.send(line);
        }
    }
}
