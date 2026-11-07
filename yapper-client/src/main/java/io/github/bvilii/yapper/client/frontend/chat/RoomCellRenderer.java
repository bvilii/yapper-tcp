package io.github.bvilii.yapper.client.frontend.chat;

import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;

public class RoomCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, false);
        if (value instanceof RoomInfo room) {
            label.setText(room.name());
        }
        label.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        return label;
    }
}