package io.github.bvilii.yapper.client.frontend.chat;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel {

    public ChatScreen(){
        setLayout(new BorderLayout());
        RoomsPanel rooms = new RoomsPanel();
        ChatPanel chat = new ChatPanel();

        JSplitPane splitView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rooms, chat);

        add(splitView, BorderLayout.CENTER);
    }
}
