package io.github.bvilii.yapper.client;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import io.github.bvilii.yapper.client.backend.Controller;
import io.github.bvilii.yapper.client.backend.ServerConnection;
import io.github.bvilii.yapper.client.frontend.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();

        ServerConnection connection = new ServerConnection(executor);
        Controller controller = new Controller(connection);
        executor.execute(controller);


        SwingUtilities.invokeLater(() -> {

            FlatInterFont.install();
            FlatLaf.setPreferredFontFamily(FlatInterFont.FAMILY);
            FlatMacDarkLaf.setup();
            UIManager.put("Component.focusWidth", 0);

            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("CheckBox.arc", 12);
            UIManager.put("List.selectionArc", 12);
            UIManager.put("List.selectionInsets", new Insets(2, 6, 2, 6));

            new MainWindow(controller).show();
        });

    }
}
