// Filename: WelcomePanel.java
package com.dish.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics; // Added
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image; // Added
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomePanel extends JPanel {

    // --- Start of new/modified code ---

    private Image backgroundImage;
    // NOTE: Ensure this image file exists. Using the login image as a placeholder.
    private static final String BACKGROUND_IMAGE_PATH = "/images/welcome.png"; 

    public WelcomePanel() {
        // Load the background image before setting up other components
        loadBackgroundImage();

        // We no longer need setBackground(Color.WHITE) as the image will cover it.
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel mainIconLabel = new JLabel(loadIconFromClasspath("/images/logo_white.png", 150, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(mainIconLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome to Dish Information Management, Admin!");
        // Adjusted font and color for better visibility on a background image
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE); 
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(welcomeLabel, gbc);
    }
    
    /**
     * Loads the background image from the specified classpath resource.
     * This is similar to the implementation in LoginView.
     */
    private void loadBackgroundImage() {
        try {
            URL imgUrl = getClass().getResource(BACKGROUND_IMAGE_PATH);
            if (imgUrl != null) {
                this.backgroundImage = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Background image not found: " + BACKGROUND_IMAGE_PATH);
                this.backgroundImage = null; // Ensure image is null if not found
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            this.backgroundImage = null; // Ensure image is null on error
        }
    }

    /**
     * Overridden to draw the background image. The image is stretched to fill
     * the entire panel. This method is called automatically by Swing when the
     * panel needs to be repainted.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call super.paintComponent first
        
        if (backgroundImage != null) {
            // Draw the image, scaling it to cover the entire panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Provide a fallback in case the image fails to load
            g.setColor(new Color(15, 15, 15)); // A dark gray, similar to LoginView
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.drawString("Error: Background image not found at " + BACKGROUND_IMAGE_PATH, 20, 20);
        }
    }
    
    // --- End of new/modified code ---


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