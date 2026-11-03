package io.github.bvilii.yapper.client.frontend;

import io.github.bvilii.yapper.client.backend.Controller;
import io.github.bvilii.yapper.client.frontend.connection.LoadingPanel;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private final JFrame frame = new JFrame("Yapper");

    public MainWindow(Controller controller){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 550);
        frame.setMinimumSize(new Dimension(800, 550));
        frame.setLocationRelativeTo(null);

        frame.add(new LoadingPanel());
    }

    public void show(){
        frame.setVisible(true);
    }
}
