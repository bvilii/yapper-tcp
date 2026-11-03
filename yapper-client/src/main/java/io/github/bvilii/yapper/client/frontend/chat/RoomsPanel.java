package io.github.bvilii.yapper.client.frontend.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class RoomsPanel extends JPanel {

    public RoomsPanel(){
        setLayout(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> roomList = new JList<>(model) {
            @Override
            protected void processMouseMotionEvent(MouseEvent e) {
                if (e.getID() != MouseEvent.MOUSE_DRAGGED) {
                    super.processMouseMotionEvent(e);
                }
            }
        };

        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new RoomCellRenderer());

        model.addElement("Csenge");
        model.addElement("Máté");
        model.addElement("Marcipán");

        roomList.setSelectionBackground(new Color(0xab0a6b));
        roomList.setSelectionForeground(Color.WHITE);
        roomList.setBackground(new Color(0xFF252525, true));
        setMinimumSize(new Dimension(270, 0));

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }
}
