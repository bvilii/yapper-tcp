package io.github.bvilii.yapper.client.frontend.chat;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.github.bvilii.yapper.client.backend.service.auth.AuthObserver;
import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.client.backend.service.room.RoomObserver;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class RoomsPanel extends JPanel implements RoomObserver, AuthObserver {
    private final RoomService roomService;
    private final DefaultListModel<RoomInfo> model = new DefaultListModel<>();
    private final JList<RoomInfo> roomList;
    private final Consumer<RoomInfo> onRoomSelected;

    public RoomsPanel(RoomService roomService, Session session, Consumer<RoomInfo> onRoomSelected) {
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
        roomList.setCellRenderer(new RoomCellRenderer(session));
        roomList.putClientProperty(FlatClientProperties.STYLE,
                "background: @yapperSidebar; "
                        + "selectionBackground: @accentColor; selectionForeground: @yapperOnAccent; "
                        + "selectionInactiveBackground: @accentColor; selectionInactiveForeground: @yapperOnAccent");
        putClientProperty(FlatClientProperties.STYLE, "background: @yapperSidebar");
        setMinimumSize(new Dimension(270, 0));

        roomList.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(createHeader(), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        // Two equal pill buttons side by side, vertically centered in a header as tall as the chat header
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        actions.add(createActionButton("Room", "New group room", CreateRoomDialog.Mode.GROUP));
        actions.add(createActionButton("DM", "New direct message", CreateRoomDialog.Mode.DIRECT));

        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        header.setPreferredSize(new Dimension(0, ChatPanel.HEADER_HEIGHT));
        GridBagConstraints fill = new GridBagConstraints();
        fill.fill = GridBagConstraints.HORIZONTAL;
        fill.weightx = 1;
        header.add(actions, fill);

        JPanel headerWithLine = new JPanel(new BorderLayout());
        headerWithLine.setOpaque(false);
        headerWithLine.add(header, BorderLayout.CENTER);
        headerWithLine.add(new JSeparator(), BorderLayout.SOUTH);
        return headerWithLine;
    }

    /** A soft, violet-tinted pill with a "+" icon, e.g. "+ Room". */
    private JButton createActionButton(String text, String tooltip, CreateRoomDialog.Mode mode) {
        FlatSVGIcon icon = new FlatSVGIcon("icons/add.svg", 12, 12);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> UIManager.getColor("Yapper.highlight")));

        JButton button = new JButton(text, icon);
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc: 999; "
                        + "background: fade(@accentColor,16%); foreground: @yapperHighlight; "
                        + "hoverBackground: fade(@accentColor,28%); pressedBackground: fade(@accentColor,38%); "
                        + "borderColor: fade(@accentColor,16%); focusedBorderColor: @yapperHighlight");
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setIconTextGap(6);
        button.setMargin(new Insets(6, 12, 6, 12));
        button.setToolTipText(tooltip);
        button.addActionListener(e -> openCreateRoomDialog(mode));
        return button;
    }

    private void openCreateRoomDialog(CreateRoomDialog.Mode mode) {
        CreateRoomDialog dialog = new CreateRoomDialog(SwingUtilities.getWindowAncestor(this), roomService, mode);
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
    public void onDmOpened(RoomInfo room) {
        SwingUtilities.invokeLater(() -> {
            // The DM may already be in the list: then just select it
            RoomInfo existing = findRoom(room.id());
            if (existing == null) {
                model.addElement(room);
                existing = room;
            }
            roomList.setSelectedValue(existing, true);
        });
    }

    private RoomInfo findRoom(long roomId) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).id() == roomId) {
                return model.get(i);
            }
        }
        return null;
    }

    @Override
    public void onRoomsLoaded(List<RoomInfo> rooms) {
        SwingUtilities.invokeLater(() -> {
            model.clear();
            model.addAll(rooms);
        });
    }

    @Override
    public void onLoggedOut(){
        SwingUtilities.invokeLater(() -> {
            model.clear();
        });
    }
}