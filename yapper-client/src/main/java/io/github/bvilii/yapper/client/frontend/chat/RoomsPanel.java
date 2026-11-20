package io.github.bvilii.yapper.client.frontend.chat;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.github.bvilii.yapper.client.backend.service.room.RoomObserver;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class RoomsPanel extends JPanel implements RoomObserver {
    private final RoomService roomService;
    private final DefaultListModel<RoomInfo> model = new DefaultListModel<>();
    private final JList<RoomInfo> roomList;
    private final Consumer<RoomInfo> onRoomSelected;

    public RoomsPanel(RoomService roomService, Consumer<RoomInfo> onRoomSelected) {
        this.roomService = roomService;
        this.onRoomSelected = onRoomSelected;
        setLayout(new BorderLayout());

        roomList = new JList<>(model) {
            @Override
            protected void processMouseMotionEvent(MouseEvent e) {
                if (e.getID() != MouseEvent.MOUSE_DRAGGED) {
                    super.processMouseMotionEvent(e);
                }
            }
        };

        roomList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            RoomInfo selected = roomList.getSelectedValue();
            if (selected != null) {
                onRoomSelected.accept(selected);
            }
        });

        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new RoomCellRenderer());
        roomList.putClientProperty(FlatClientProperties.STYLE,
                "background: @yapperSurface; "
                        + "selectionBackground: @accentColor; selectionForeground: @yapperOnAccent; "
                        + "selectionInactiveBackground: @accentColor; selectionInactiveForeground: @yapperOnAccent");
        putClientProperty(FlatClientProperties.STYLE, "background: @yapperSurface");
        setMinimumSize(new Dimension(270, 0));

        roomList.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(createHeader(), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 12));
        header.setPreferredSize(new Dimension(0, ChatPanel.HEADER_HEIGHT));

        JLabel title = new JLabel("Yaps");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");
        title.putClientProperty(FlatClientProperties.STYLE, "foreground: @yapperHighlight");
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);

        FlatSVGIcon addIcon = new FlatSVGIcon("icons/add.svg", 16, 16);
        addIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> UIManager.getColor("Yapper.highlight")));

        JButton addButton = new JButton(addIcon);
        addButton.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        addButton.putClientProperty(FlatClientProperties.STYLE, "arc: 999");
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.setPreferredSize(new Dimension(34, 34));
        addButton.setToolTipText("New chat");
        addButton.addActionListener(e -> openCreateRoomDialog());
        actions.add(addButton);

        header.add(actions, BorderLayout.EAST);

        JPanel headerWithLine = new JPanel(new BorderLayout());
        headerWithLine.setOpaque(false);
        headerWithLine.add(header, BorderLayout.CENTER);
        headerWithLine.add(new JSeparator(), BorderLayout.SOUTH);
        return headerWithLine;
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

    @Override
    public void onRoomsLoaded(List<RoomInfo> rooms) {
        SwingUtilities.invokeLater(() -> {
            model.clear();
            model.addAll(rooms);
        });
    }
}