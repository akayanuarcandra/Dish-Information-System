// Filename: LoginView.java
package com.dish.ui;

import java.awt.BorderLayout; // Added
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font; // Add this import
import java.awt.FontMetrics;
import java.awt.Graphics; // Added
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets; // Added
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent; // Add this import
import java.net.URL; // Add this import

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton; // Added
import javax.swing.JFrame;
import javax.swing.JLabel; // Added
import javax.swing.JOptionPane;
import javax.swing.JPanel; // Added
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border; // Added
import javax.swing.border.LineBorder;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton; // Declare loginButton as a class member

    private static final String USERNAME_PLACEHOLDER = "Enter your username";
    private static final String PASSWORD_PLACEHOLDER = "Enter your password";
    private static final String IMAGE_PATH = "/images/login.png"; // Ensure this image exists
    private static final String LOGO_PATH = "/images/logo.png"; // Ensure this logo exists

    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_PLACEHOLDER_TEXT = Color.GRAY;
    private static final Color COLOR_INPUT_TEXT = Color.BLACK;
    private static final Color COLOR_LOGIN_BUTTON_BG = new Color(40, 169, 145);
    private static final Color COLOR_EXIT_BUTTON_BG = new Color(189, 7, 7);
    private static final Color COLOR_BUTTON_FG = Color.WHITE;
    private static final Color COLOR_FIELD_BORDER = Color.LIGHT_GRAY;
    private static final Color COLOR_IMAGE_PANEL_BG = new Color(230, 230, 230); // Example background for image panel

    private static final int PADDING_EMPTY_BORDER = 10;
    private static final int PADDING_FIELD_BOTTOM = 5;
    private static final int PADDING_BUTTON_PANEL_TOP = 50;
    private static final int BUTTON_HEIGHT_INCREASE = 10;
    private static final int FORM_PANEL_PADDING = 75; // Padding for the form panel itself
    private static final int IMAGE_PREFERRED_WIDTH = 1000; // Adjust as needed

    public LoginView() {
        super("Dish Information Management");
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(IMAGE_PREFERRED_WIDTH + 250 + (FORM_PANEL_PADDING * 2), 800));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BACKGROUND);

        JPanel imagePanel = createImagePanel(); // This will now return our custom panel
        mainPanel.add(imagePanel, BorderLayout.WEST);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        getRootPane().requestFocusInWindow();
        setResizable(false);
        // Set the login button as the default button for the root pane
        if (this.loginButton != null) {
            getRootPane().setDefaultButton(this.loginButton);
        }
    }

    // Custom panel to draw a background image that fills its bounds
    private class BackgroundImagePanel extends JPanel {
        private Image image;

        public BackgroundImagePanel(String imagePath) {
            try {
                URL imgUrl = getClass().getResource(imagePath);
                if (imgUrl != null) {
                    this.image = new ImageIcon(imgUrl).getImage();
                } else {
                    System.err.println("Image not found: " + imagePath);
                    this.image = null; // Ensure image is null if not found
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                this.image = null; // Ensure image is null on error
            }
            // Set the preferred width. Height will be determined by the layout.
            setPreferredSize(new Dimension(IMAGE_PREFERRED_WIDTH, 0));
            setBackground(COLOR_IMAGE_PANEL_BG); // Set a background in case image fails or is transparent
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                // This is the version from your visible code, which stretches the image.
                // If you want "cover" or "fill height while maintaining aspect ratio",
                // you'll need to use one of the more complex paintComponent versions previously provided.
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback text if image is not loaded
                g.setColor(Color.DARK_GRAY); // Changed error text color
                String errorMsg = "Image error: " + IMAGE_PATH; // More informative
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(errorMsg);
                // Center the error message
                int x = (getWidth() - stringWidth) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                if (x < 5) x = 5; // Prevent drawing off-screen if panel is too narrow
                g.drawString(errorMsg, x, y);
            }
        }
    }

    private JPanel createImagePanel() {
        // Simply return an instance of our custom BackgroundImagePanel
        return new BackgroundImagePanel(IMAGE_PATH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(FORM_PANEL_PADDING, FORM_PANEL_PADDING, FORM_PANEL_PADDING, FORM_PANEL_PADDING));
        panel.setBackground(COLOR_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1; 

        // Logo Label
        JLabel logoLabel = new JLabel();
        try {
            URL logoUrl = getClass().getResource(LOGO_PATH);
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image logoImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(logoImage);
                logoLabel.setIcon(logoIcon);
            } else {
                System.err.println("Logo not found");
                logoLabel.setText("Logo"); 
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            logoLabel.setText("Logo Error"); 
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.weightx = 1.0; 
        gbc.insets = new Insets(0, 0, 10, 0); 
        panel.add(logoLabel, gbc);

        // Title Label
        JLabel titleLabel = new JLabel("Dish Information Management");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 2; 

        // Username Label
        JLabel usernameLabel = new JLabel("Username");
        gbc.gridx = 0;
        gbc.gridy = 2; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; 
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(usernameLabel, gbc);

        // Username Text Field
        usernameField = createUsernameField();
        gbc.gridx = 0;
        gbc.gridy = 3; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        gbc.insets = new Insets(0, 0, PADDING_FIELD_BOTTOM, 0);
        panel.add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password");
        gbc.gridx = 0;
        gbc.gridy = 4; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; 
        gbc.insets = new Insets(PADDING_FIELD_BOTTOM, 0, 5, 0);
        panel.add(passwordLabel, gbc);

        // Password Field
        passwordField = createPasswordField();
        gbc.gridx = 0;
        gbc.gridy = 5; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        gbc.insets = new Insets(0, 0, PADDING_FIELD_BOTTOM, 0);
        panel.add(passwordField, gbc);

        // Login Button
        this.loginButton = createLoginButton(); // Assign to the class member
        gbc.gridx = 0;
        gbc.gridy = 6; 
        gbc.gridwidth = 1; 
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(PADDING_BUTTON_PANEL_TOP, 0, 0, 5); 
        panel.add(this.loginButton, gbc); // Add the class member instance

        // Exit Button ("X")
        JButton exitButton = createExitButton();
        gbc.gridx = 1; 
        gbc.gridy = 6; 
        gbc.gridwidth = 1;
        gbc.weightx = 0.0; 
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.EAST; 
        gbc.insets = new Insets(PADDING_BUTTON_PANEL_TOP, 0, 0, 0); 
        panel.add(exitButton, gbc);
        
        // Spacer Panel for 50px space below buttons
        JPanel bottomSpacer = new JPanel();
        bottomSpacer.setOpaque(false); // Make it transparent
        bottomSpacer.setPreferredSize(new Dimension(1, 100)); // Width 1, Height 50
        gbc.gridx = 0;
        gbc.gridy = 7; // Row after the buttons
        gbc.gridwidth = 2; // Span both columns
        gbc.weightx = 0;
        gbc.weighty = 0; // Don't let it expand vertically beyond its preferred size
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; // Or NORTH, doesn't matter much for a fixed size spacer
        gbc.insets = new Insets(0, 0, 0, 0); // No insets for the spacer itself
        panel.add(bottomSpacer, gbc);


        // Action Listeners
        this.loginButton.addActionListener(e -> handleLogin()); // Use the class member instance
        exitButton.addActionListener(e -> System.exit(0));

        return panel;
    }


    private JTextField createUsernameField() {
        JTextField field = new JTextField(USERNAME_PLACEHOLDER);
        styleTextField(field, USERNAME_PLACEHOLDER, COLOR_PLACEHOLDER_TEXT, COLOR_INPUT_TEXT);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(PASSWORD_PLACEHOLDER);
        stylePasswordField(field, PASSWORD_PLACEHOLDER, COLOR_PLACEHOLDER_TEXT, COLOR_INPUT_TEXT);
        return field;
    }

    private void styleTextField(JTextField field, String placeholder, Color placeholderColor, Color inputColor) {
        Border lineBorder = new LineBorder(COLOR_FIELD_BORDER, 1, false);
        Border emptyBorder = BorderFactory.createEmptyBorder(PADDING_EMPTY_BORDER, PADDING_EMPTY_BORDER, PADDING_EMPTY_BORDER, PADDING_EMPTY_BORDER);
        field.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        field.setForeground(placeholderColor);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(inputColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(placeholderColor);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void stylePasswordField(JPasswordField field, String placeholder, Color placeholderColor, Color inputColor) {
        Border lineBorder = new LineBorder(COLOR_FIELD_BORDER, 1, false);
        Border emptyBorder = BorderFactory.createEmptyBorder(PADDING_EMPTY_BORDER, PADDING_EMPTY_BORDER, PADDING_EMPTY_BORDER, PADDING_EMPTY_BORDER);
        field.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(placeholderColor);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(inputColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(placeholderColor);
                } else {
                     field.setEchoChar('•');
                }
            }
        });
    }


    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_BUTTON_FG);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        Dimension preferredSize = button.getPreferredSize();
        button.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + BUTTON_HEIGHT_INCREASE));
        return button;
    }

    private JButton createLoginButton() {
        return createStyledButton("Login", COLOR_LOGIN_BUTTON_BG);
    }

    private JButton createExitButton() {
        JButton button = createStyledButton("✖", COLOR_EXIT_BUTTON_BG); // Text is "X"
        // Make it square based on its calculated preferred height
        Dimension currentPreferredSize = button.getPreferredSize();
        button.setPreferredSize(new Dimension(currentPreferredSize.height, currentPreferredSize.height));
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (USERNAME_PLACEHOLDER.equals(username) || PASSWORD_PLACEHOLDER.equals(String.valueOf(passwordField.getPassword()))) {
             JOptionPane.showMessageDialog(this,
                    "Please enter your username and password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("admin".equals(username) && "password".equals(password)) {
            this.dispose(); // Close the login window
            new MenuManager().setVisible(true); // Open the MenuManager window
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}