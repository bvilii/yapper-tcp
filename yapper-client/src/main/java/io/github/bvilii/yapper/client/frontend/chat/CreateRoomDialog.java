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
import java.util.ArrayList;
import java.util.List;

public class CreateRoomDialog extends JDialog implements RoomObserver {

    /** What the user wants to start: a named room with several people, or a chat with one person. */
    public enum Mode { GROUP, DIRECT }

    private static final int MAX_ROOM_NAME_LENGTH = 64;

    private final transient RoomService roomService;
    private final Mode mode;
    private final List<String> people = new ArrayList<>();

    private final JTextField roomNameField = new JTextField(24);
    private final JTextField usernameField = new JTextField(18);
    private final JButton createButton;
    private final JLabel errorLabel = new JLabel(" ");

    private final JPanel peoplePanel = new JPanel();

    public CreateRoomDialog(Window owner, RoomService roomService, Mode mode) {
        super(owner, mode == Mode.GROUP ? "New room" : "New direct message", ModalityType.APPLICATION_MODAL);
        this.roomService = roomService;
        this.mode = mode;
        this.createButton = new JButton(mode == Mode.GROUP ? "Create room" : "Start chat");

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (mode == Mode.GROUP) {
            buildGroupForm(content);
        } else {
            buildDirectForm(content);
        }

        errorLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: @yapperError");
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Buttons ---
        createButton.putClientProperty(FlatClientProperties.STYLE,
                "background: @accentColor; foreground: @yapperOnAccent; "
                        + "disabledBackground: darken(@accentColor,8%); disabledText: fade(@yapperOnAccent,70%)");
        createButton.addActionListener(e -> submit());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(cancelButton);
        buttons.add(createButton);

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

    // ---------- Forms ----------

    /** Room name on top, then the people to add, each shown as a removable row. */
    private void buildGroupForm(JPanel content) {
        JLabel roomNameLabel = new JLabel("Room name");
        roomNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "The Yappers");
        roomNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomNameField.getPreferredSize().height));

        JLabel peopleLabel = new JLabel("Add yappers");
        peopleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");

        JButton addButton = new JButton("Add");
        addButton.putClientProperty(FlatClientProperties.STYLE,
                "background: fade(@accentColor,16%); foreground: @yapperHighlight; "
                        + "hoverBackground: fade(@accentColor,28%); pressedBackground: fade(@accentColor,38%); "
                        + "borderColor: fade(@accentColor,16%)");
        addButton.addActionListener(e -> addPerson());
        usernameField.addActionListener(e -> addButton.doClick());   // Enter in the field adds the person

        JPanel addRow = new JPanel(new BorderLayout(8, 0));
        addRow.setOpaque(false);
        addRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        addRow.add(usernameField, BorderLayout.CENTER);
        addRow.add(addButton, BorderLayout.EAST);
        addRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, addRow.getPreferredSize().height));

        peoplePanel.setLayout(new BoxLayout(peoplePanel, BoxLayout.Y_AXIS));
        peoplePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        peoplePanel.setOpaque(false);

        content.add(roomNameLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(roomNameField);
        content.add(Box.createVerticalStrut(16));
        content.add(peopleLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(addRow);
        content.add(Box.createVerticalStrut(12));
        content.add(peoplePanel);
    }

    /** Just one username. Enter in the field starts the chat (default button). */
    private void buildDirectForm(JPanel content) {
        JLabel label = new JLabel("Who do you want to yap with?");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, usernameField.getPreferredSize().height));

        content.add(label);
        content.add(Box.createVerticalStrut(6));
        content.add(usernameField);
    }

    // ---------- Group: adding and removing people ----------

    private void addPerson() {
        String name = usernameField.getText().trim();
        if (name.isEmpty()) {
            errorLabel.setText("Type a username first");
            return;
        }
        boolean alreadyAdded = people.stream().anyMatch(existing -> existing.equalsIgnoreCase(name));
        if (alreadyAdded) {
            errorLabel.setText(name + " is already added");
            return;
        }

        people.add(name);
        peoplePanel.add(createPersonRow(name));
        usernameField.setText("");
        errorLabel.setText(" ");
        refreshLayout();
        usernameField.requestFocusInWindow();
    }

    /** One rounded row like the rooms in the sidebar: the name on the left, a remove button on the right. */
    private JPanel createPersonRow(String name) {
        JLabel label = new JLabel(name);

        JButton removeButton = new JButton("✕");
        removeButton.putClientProperty(FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        removeButton.setFocusable(false);
        removeButton.setToolTipText("Remove " + name);

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $Yapper.roomCardBackground");
        row.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 2));
        row.add(label, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);

        // Transparent wrapper that puts a small gap under each row
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        holder.setAlignmentX(Component.LEFT_ALIGNMENT);
        holder.add(row);
        holder.setMaximumSize(new Dimension(Integer.MAX_VALUE, holder.getPreferredSize().height));

        removeButton.addActionListener(e -> {
            people.remove(name);
            peoplePanel.remove(holder);
            refreshLayout();
        });
        return holder;
    }

    private void refreshLayout() {
        peoplePanel.revalidate();
        peoplePanel.repaint();
        pack();
    }

    // ---------- Submit ----------

    private void submit() {
        if (mode == Mode.GROUP) {
            submitGroup();
        } else {
            submitDirect();
        }
    }

    private void submitGroup() {
        String roomName = roomNameField.getText().trim();
        if (roomName.isEmpty()) {
            errorLabel.setText("Room name is required");
            return;
        }
        if (roomName.length() > MAX_ROOM_NAME_LENGTH) {
            errorLabel.setText("Room name must be at most " + MAX_ROOM_NAME_LENGTH + " characters");
            return;
        }
        if (people.isEmpty()) {
            errorLabel.setText("Add at least one yapper");
            return;
        }

        errorLabel.setText(" ");
        createButton.setEnabled(false);
        roomService.createRoom(roomName, List.copyOf(people));
    }

    private void submitDirect() {
        String name = usernameField.getText().trim();
        if (name.isEmpty()) {
            errorLabel.setText("Type a username first");
            return;
        }

        errorLabel.setText(" ");
        createButton.setEnabled(false);
        roomService.openDm(name);
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

    @Override
    public void onDmOpened(RoomInfo room) {
        SwingUtilities.invokeLater(this::dispose);
    }

    @Override
    public void onOpenDmFailed(String reason) {
        SwingUtilities.invokeLater(() -> {
            errorLabel.setText(reason);
            createButton.setEnabled(true);
        });
    }
}
