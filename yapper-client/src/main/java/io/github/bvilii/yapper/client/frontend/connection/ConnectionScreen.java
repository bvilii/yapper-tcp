package io.github.bvilii.yapper.client.frontend.connection;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class ConnectionScreen extends JPanel {

    public ConnectionScreen(){
        //ConnectionPanel config
        setLayout(new GridBagLayout());

        //card shaped background panel for the content
        JPanel backgroundCard = new JPanel();
        backgroundCard.setLayout(new BoxLayout(backgroundCard, BoxLayout.Y_AXIS));
        backgroundCard.putClientProperty(FlatClientProperties.STYLE, "arc: 24; background: #010736");
        backgroundCard.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));


        //fill the card with content
        JLabel connectionLostLabel = new JLabel("CONNECTION LOST");
        connectionLostLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");
        connectionLostLabel.setForeground(new Color(0xFCF1D0));
        connectionLostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundCard.add(connectionLostLabel);
        backgroundCard.add(Box.createVerticalStrut(10));



        JLabel tryAgainLabel = new JLabel("Try to reconnect to yapper");
        tryAgainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tryAgainLabel.setForeground(new Color(0xFCF1D0));
        backgroundCard.add(tryAgainLabel);
        backgroundCard.add(Box.createVerticalStrut(10));



        JButton reconnectButton = new JButton("RECONNECT");
        reconnectButton.setMargin(new Insets(10, 10, 10, 10));
        reconnectButton.setForeground(new Color(0xFCF1D0));
        reconnectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reconnectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        reconnectButton.setBackground(new Color(0x22396F));
        reconnectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, reconnectButton.getPreferredSize().height));
        backgroundCard.add(reconnectButton);
        backgroundCard.add(Box.createVerticalStrut(30));


        add(backgroundCard);
    }
}
