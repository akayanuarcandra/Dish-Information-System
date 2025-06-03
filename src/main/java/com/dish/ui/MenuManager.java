package com.dish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class MenuManager extends JFrame {

    // Define colors (approximated from the image)
    private static final Color SIDEBAR_BACKGROUND = new Color(40, 169, 145); // Teal
    private static final Color SIDEBAR_TEXT_COLOR = Color.WHITE;
    private static final Color MAIN_CONTENT_BACKGROUND = Color.WHITE; // White background for main content
    private static final Color MAIN_CONTENT_TEXT_COLOR = Color.BLACK; // Black text for main content
    
    // Define icon paths
    private static final String ICON_PATH = "/images/logo_white.png";
    private static final String LOGO_PATH = "/images/logo.png"; // Placeholder for main icon

    // Define fonts
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font WELCOME_FONT = new Font("Arial", Font.PLAIN, 18);

    public MenuManager() {
        setTitle("Dish Information Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1440, 800);
        setLocationRelativeTo(null); // Center the window
        // setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
        setLayout(new BorderLayout());

        // --- Sidebar Panel ---
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // --- Main Content Panel ---
        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SIDEBAR_BACKGROUND);
        panel.setPreferredSize(new Dimension(250, 0)); // Width, height is flexible
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 15, 10, 15); // top, left, bottom, right padding

        // 1. Logo and Title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false); // Transparent background

        JLabel logoLabel = new JLabel(loadIcon(ICON_PATH, 32, 32));
        headerPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("<html>Dish Information<br>Management</html>");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(SIDEBAR_TEXT_COLOR);
        headerPanel.add(titleLabel);
        
        gbc.insets = new Insets(20, 15, 20, 15); // More padding for header
        panel.add(headerPanel, gbc);
        gbc.insets = new Insets(10, 15, 10, 15); // Reset padding

        // 2. Menu Items
        panel.add(createSidebarButton("Search Dish", "/icons/search.png"), gbc);
        panel.add(createSidebarButton("Add Dish", "/icons/add.png"), gbc);
        panel.add(createSidebarButton("Modify Dish", "/icons/modify.png"), gbc);
        panel.add(createSidebarButton("Delete Dish", "/icons/delete.png"), gbc);
        panel.add(createSidebarButton("Import Dish", "/icons/import.png"), gbc);
        panel.add(createSidebarButton("Export Dish", "/icons/export.png"), gbc);

        // Spacer to push logout to the bottom
        gbc.weighty = 1.0; // This component will take up all extra vertical space
        panel.add(new JLabel(), gbc); // Empty label as a spacer
        gbc.weighty = 0; // Reset weighty

        // 3. Log Out Button
        JButton logoutButton = createSidebarButton("Log Out", "/icons/logout.png");
        logoutButton.addActionListener(e -> {
            // Action for logout
            this.dispose(); // Close the current MenuManager window
            new LoginView().setVisible(true); // Open a new LoginView window
        });
        gbc.anchor = GridBagConstraints.SOUTHWEST; // Anchor to bottom-left
        gbc.insets = new Insets(10, 15, 20, 15); // Padding for logout
        panel.add(logoutButton, gbc);

        return panel;
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton(text);
        ImageIcon icon = loadIcon(iconPath, 20, 20);
        if (icon != null) {
            button.setIcon(icon);
        }
        button.setFont(DEFAULT_FONT);
        button.setForeground(SIDEBAR_TEXT_COLOR);
        button.setBackground(SIDEBAR_BACKGROUND); // For hover/pressed effects if needed
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Makes background transparent
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15); // Space between icon and text
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(5, 10, 5, 0)); // Top/bottom padding for the button itself

        // Optional: Add hover effect (simple one)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(SIDEBAR_TEXT_COLOR.brighter()); // Slightly brighter text
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(SIDEBAR_TEXT_COLOR);
            }
        });

        return button;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
        panel.setBackground(MAIN_CONTENT_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Large Central Icon
        JLabel mainIconLabel = new JLabel(loadIcon(LOGO_PATH, 150, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // Bottom margin
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(mainIconLabel, gbc);

        // 2. Welcome Text
        JLabel welcomeLabel = new JLabel("Welcome to Dish Information Management, Admin!");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(MAIN_CONTENT_TEXT_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // Reset insets
        panel.add(welcomeLabel, gbc);

        return panel;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            try {
                BufferedImage img = ImageIO.read(imgUrl);
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } catch (IOException e) {
                System.err.println("Couldn't load icon: " + path);
                e.printStackTrace();
            }
        } else {
            System.err.println("Icon resource not found: " + path);
            // Return a placeholder or default icon if needed
            // For now, just print error and return null
        }
        return null;
    }
}