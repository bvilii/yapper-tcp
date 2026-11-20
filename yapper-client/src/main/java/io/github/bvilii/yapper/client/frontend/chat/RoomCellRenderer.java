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
        label.setFont(roomNameFont(list));
        label.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        return label;
    }

    // Semibold and one point larger than the list font; full bold stays reserved for unread rooms
    private static Font roomNameFont(JList<?> list) {
        Font semibold = UIManager.getFont("semibold.font");
        Font base = semibold != null ? semibold : list.getFont().deriveFont(Font.BOLD);
        return base.deriveFont(list.getFont().getSize2D() + 1f);
    }
}