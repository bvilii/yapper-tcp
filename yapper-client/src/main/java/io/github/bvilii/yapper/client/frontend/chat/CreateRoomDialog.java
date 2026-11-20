package io.github.bvilii.yapper.client.frontend.chat;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.bvilii.yapper.client.backend.service.room.RoomObserver;
import io.github.bvilii.yapper.client.backend.service.room.RoomService;
import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class CreateRoomDialog extends JDialog implements RoomObserver {
    private static final int MAX_ROOM_NAME_LENGTH = 64;

    private final RoomService roomService;
    private final JTextField roomNameField = new JTextField(24);
    private final JTextField usernamesField = new JTextField(24);
    private final JButton createButton = new JButton("Create");
    private final JLabel errorLabel = new JLabel(" ");

    public CreateRoomDialog(Window owner, RoomService roomService) {
        super(owner, "New chat", ModalityType.APPLICATION_MODAL);
        this.roomService = roomService;

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel roomNameLabel = new JLabel("Room name");
        roomNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernamesLabel = new JLabel("Usernames (comma separated)");
        usernamesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamesField.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: @yapperError");
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createButton.putClientProperty(FlatClientProperties.STYLE,
                "background: @accentColor; foreground: @yapperOnAccent; "
                        + "disabledBackground: darken(@accentColor,8%); disabledText: fade(@yapperOnAccent,70%)");

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        createButton.addActionListener(e -> submit());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(cancelButton);
        buttons.add(createButton);

        content.add(roomNameLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(roomNameField);
        content.add(Box.createVerticalStrut(12));
        content.add(usernamesLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(usernamesField);
        content.add(Box.createVerticalStrut(6));
        content.add(errorLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(buttons);
        setContentPane(content);

        getRootPane().setDefaultButton(createButton);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                roomService.removeObserver(CreateRoomDialog.this);
            }
        });

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    private void submit() {
        String roomName = roomNameField.getText().trim();
        List<String> usernames = Arrays.stream(usernamesField.getText().split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .toList();

        if (roomName.isEmpty()) {
            errorLabel.setText("Room name is required");
            return;
        }
        if (roomName.length() > MAX_ROOM_NAME_LENGTH) {
            errorLabel.setText("Room name must be at most " + MAX_ROOM_NAME_LENGTH + " characters");
            return;
        }
        if (usernames.isEmpty()) {
            errorLabel.setText("Add at least one username");
            return;
        }

        errorLabel.setText(" ");
        createButton.setEnabled(false);
        roomService.createRoom(roomName, usernames);
    }

    @Override
    public void onRoomCreated(RoomInfo room) {
        SwingUtilities.invokeLater(this::dispose);
    }

    @Override
    public void onCreateRoomFailed(String reason) {
        SwingUtilities.invokeLater(() -> {
            errorLabel.setText(reason);
            createButton.setEnabled(true);
        });
    }
}