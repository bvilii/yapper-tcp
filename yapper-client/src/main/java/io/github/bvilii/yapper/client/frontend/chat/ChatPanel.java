package io.github.bvilii.yapper.client.frontend.chat;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    public ChatPanel(){
        setLayout(new BorderLayout());


        JPanel backgroundCard = new JPanel();
        backgroundCard.setLayout(new BoxLayout(backgroundCard, BoxLayout.Y_AXIS));
        backgroundCard.putClientProperty(FlatClientProperties.STYLE, "arc: 24; background: lighten(@background,5%)");
        backgroundCard.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel brand = new JLabel("Yapperaaaaaaaaaaaaaaaaaaa");
        brand.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        brand.setForeground(new Color(0x29B8A7));
        backgroundCard.add(brand);
        backgroundCard.add(Box.createVerticalStrut(20));

        add(backgroundCard);
    }
}
