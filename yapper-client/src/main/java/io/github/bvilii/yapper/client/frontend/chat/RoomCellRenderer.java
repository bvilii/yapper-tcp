package io.github.bvilii.yapper.client.frontend.chat;

import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.common.model.RoomInfo;

import javax.swing.*;
import java.awt.*;

public class RoomCellRenderer extends DefaultListCellRenderer {
    private final Session session;
    private boolean drawCard;

    public RoomCellRenderer(Session session) {
        this.session = session;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, false);
        if (value instanceof RoomInfo room) {
            label.setText(RoomTitle.of(room, session));
        }
        label.setFont(roomNameFont(list));
        label.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Selected rooms keep FlatLaf's rounded accent selection, the others get a card
        drawCard = !isSelected;
        label.setOpaque(!drawCard);
        return label;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (drawCard) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UIManager.getColor("Yapper.roomCardBackground"));
            // Same margins and corner radius as the selection, so card and selection line up exactly
            Insets insets = UIManager.getInsets("List.selectionInsets");
            int arc = UIManager.getInt("List.selectionArc");
            g2.fillRoundRect(insets.left, insets.top,
                    getWidth() - insets.left - insets.right,
                    getHeight() - insets.top - insets.bottom,
                    arc, arc);
            g2.dispose();
        }
        super.paintComponent(g);
    }

    // Semibold and one point larger than the list font; full bold stays reserved for unread rooms
    private static Font roomNameFont(JList<?> list) {
        Font semibold = UIManager.getFont("semibold.font");
        Font base = semibold != null ? semibold : list.getFont().deriveFont(Font.BOLD);
        return base.deriveFont(list.getFont().getSize2D() + 1f);
    }
}
