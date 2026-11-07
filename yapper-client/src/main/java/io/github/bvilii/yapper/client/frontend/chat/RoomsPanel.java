package io.github.bvilii.yapper.client.frontend.chat;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.service.room.RoomObserver;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class RoomsPanel extends JPanel implements RoomObserver {
    private final RoomService roomService;
    private final DefaultListModel<RoomInfo> model = new DefaultListModel<>();
    private final JList<RoomInfo> roomList;

    public RoomsPanel(RoomService roomService) {
        this.roomService = roomService;
        setLayout(new BorderLayout());

        roomList = new JList<>(model) {
            @Override
            protected void processMouseMotionEvent(MouseEvent e) {
                if (e.getID() != MouseEvent.MOUSE_DRAGGED) {
                    super.processMouseMotionEvent(e);
                }
            }
        };

        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new RoomCellRenderer());
        roomList.setSelectionBackground(new Color(0xab0a6b));
        roomList.setSelectionForeground(Color.WHITE);
        roomList.setBackground(new Color(0xFF252525, true));
        setMinimumSize(new Dimension(270, 0));

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(createHeader(), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 8));

        JLabel title = new JLabel("Yaps");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);

        JButton addButton = new JButton("+");
        addButton.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        addButton.setFont(addButton.getFont().deriveFont(Font.BOLD, 18f));
        addButton.setToolTipText("New chat");
        addButton.addActionListener(e -> openCreateRoomDialog());
        actions.add(addButton);

        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private void openCreateRoomDialog() {
        CreateRoomDialog dialog = new CreateRoomDialog(SwingUtilities.getWindowAncestor(this), roomService);
        roomService.addObserver(dialog);
        dialog.setVisible(true);
    }

    @Override
    public void onRoomCreated(RoomInfo room) {
        SwingUtilities.invokeLater(() -> {
            model.addElement(room);
            roomList.setSelectedValue(room, true);
        });
    }
}