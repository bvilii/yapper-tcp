package io.github.bvilii.yapper.client.frontend.chat;

import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.common.model.MessageInfo;

import javax.swing.*;
import java.awt.*;

public class MessageCellRenderer extends DefaultListCellRenderer {
    private final Session session;

    public MessageCellRenderer(Session session) {
        this.session = session;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, false, false);

        if (value instanceof MessageInfo message) {
            boolean mine = session.isMe(message.sender());
            label.setText(mine ? message.text() : message.sender() + ": " + message.text());
            label.setHorizontalAlignment(mine ? SwingConstants.RIGHT : SwingConstants.LEFT);
        }

        label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return label;
    }
}