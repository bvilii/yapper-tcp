package io.github.bvilii.yapper.client.frontend.connection;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    private static final String BASE_TEXT = "Connecting";
    private static final String DOT = " .";
    private static final int MAX_DOTS = 3;

    private final JLabel label = new JLabel();
    private int dots = 0;

    public LoadingPanel() {
        setLayout(new GridBagLayout());

        label.setFont(label.getFont().deriveFont(Font.PLAIN, 36f));
        FontMetrics fm = label.getFontMetrics(label.getFont());
        Dimension size = new Dimension(fm.stringWidth(BASE_TEXT + DOT.repeat(MAX_DOTS)) + 10, fm.getHeight());
        label.setMinimumSize(size);
        label.setPreferredSize(size);
        label.setMaximumSize(size);
        label.setText(BASE_TEXT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(45);
        bar.putClientProperty(FlatClientProperties.STYLE, "arc: 999; foreground: #32D74B");
        Dimension barSize = new Dimension(390, 10);
        bar.setPreferredSize(barSize);
        bar.setMaximumSize(barSize);
        bar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);
        column.add(label);
        column.add(Box.createVerticalStrut(35));
        column.add(bar);

        add(column);

        new Timer(600, e -> {
            dots = (dots + 1) % (MAX_DOTS + 1);
            label.setText(BASE_TEXT + DOT.repeat(dots));
        }).start();
    }
}