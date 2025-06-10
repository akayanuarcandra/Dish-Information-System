package com.dish.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomePanel extends JPanel {

    public WelcomePanel() {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel mainIconLabel = new JLabel(loadIconFromClasspath("/images/logo.png", 150, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(mainIconLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome to Dish Information Management, Admin!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        welcomeLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(welcomeLabel, gbc);
    }

    private ImageIcon loadIconFromClasspath(String path, int width, int height) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            try {
                Image img = ImageIO.read(imgUrl);
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException e) {
                System.err.println("Couldn't load icon from classpath: " + path);
            }
        }
        return null;
    }
}