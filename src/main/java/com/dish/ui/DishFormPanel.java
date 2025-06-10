package com.dish.ui;

import com.dish.dao.DishDAO;
import com.dish.model.Dish;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DishFormPanel extends JPanel {

    private final DishDAO dishDAO;
    private final MenuManager mainFrame;
    private final Dish dishToEdit; // Null if in "Add" mode
    private final boolean isEditMode;

    // --- Form UI Components ---
    private final JTextField nameField = new JTextField(30);
    private final JTextField typeField = new JTextField(30);
    private final JTextField priceField = new JTextField(10);
    private final JTextArea ingredientsArea = new JTextArea(3, 30);
    private final JTextArea introArea = new JTextArea(3, 30);
    private final JTextField photoPathField = new JTextField(25);
    private final JLabel imagePreviewLabel = new JLabel("No Preview", SwingConstants.CENTER);

    private static final int PREVIEW_IMAGE_WIDTH = 100;
    private static final int PREVIEW_IMAGE_HEIGHT = 100;

    public DishFormPanel(DishDAO dishDAO, MenuManager mainFrame, Dish dishToEdit) {
        this.dishDAO = dishDAO;
        this.mainFrame = mainFrame;
        this.dishToEdit = dishToEdit;
        this.isEditMode = (dishToEdit != null);

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        buildForm();

        // If we are in "Edit" mode, populate the form fields with the dish's data
        if (isEditMode) {
            populateForm();
        }
    }

    private void buildForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Title ---
        JLabel titleLabel = new JLabel(isEditMode ? "Edit Dish" : "Add New Dish");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 20, 5); // Add more space below title
        add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5); // Reset insets

        // --- Form Fields ---
        int y = 1; // Start at row 1
        addFormField(new JLabel("Name:"), nameField, y++);
        addFormField(new JLabel("Type:"), typeField, y++);
        addFormField(new JLabel("Price (¥):"), priceField, y++);

        // Text Areas need special handling for scroll panes
        gbc.gridy = y++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHWEST; add(new JLabel("Ingredients:"), gbc);
        ingredientsArea.setLineWrap(true); ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsArea);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        add(ingredientsScrollPane, gbc);
        resetGbc(gbc);

        gbc.gridy = y++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHWEST; add(new JLabel("Introduction:"), gbc);
        introArea.setLineWrap(true); introArea.setWrapStyleWord(true);
        JScrollPane introScrollPane = new JScrollPane(introArea);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        add(introScrollPane, gbc);
        resetGbc(gbc);

        // Photo Path Row
        gbc.gridy = y++; gbc.gridx = 0; add(new JLabel("Photo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; add(photoPathField, gbc);
        photoPathField.setEditable(false);
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> browseForImage());
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; add(browseButton, gbc);
        resetGbc(gbc);

        // Image Preview Row
        gbc.gridy = y++; gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        imagePreviewLabel.setPreferredSize(new Dimension(PREVIEW_IMAGE_WIDTH, PREVIEW_IMAGE_HEIGHT));
        imagePreviewLabel.setBorder(BorderFactory.createEtchedBorder());
        add(imagePreviewLabel, gbc);
        resetGbc(gbc);

        // --- Action Buttons ---
        JButton saveButton = new JButton(isEditMode ? "Save Changes" : "Add Dish");
        saveButton.addActionListener(e -> saveDish());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.showSearchDishPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = y; gbc.gridx = 0; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.1; // Push content up
        add(buttonPanel, gbc);
    }
    
    // Helper to add a standard label-field row
    private void addFormField(JLabel label, JComponent field, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = y;
        
        gbc.gridx = 0;
        add(label, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(field, gbc);
    }

    // Helper to reset GBC to defaults for the next row
    private void resetGbc(GridBagConstraints gbc) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
    }

    private void populateForm() {
        nameField.setText(dishToEdit.getName());
        typeField.setText(dishToEdit.getType());
        priceField.setText(String.format("%.2f", dishToEdit.getPrice()));
        ingredientsArea.setText(dishToEdit.getIngredients());
        introArea.setText(dishToEdit.getIntroduction());
        photoPathField.setText(dishToEdit.getPhotoPath());
        updateImagePreview(dishToEdit.getPhotoPath());
    }

    private void browseForImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Photo File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            photoPathField.setText(path);
            updateImagePreview(path);
        }
    }

    private void updateImagePreview(String path) {
        if (path != null && !path.isEmpty()) {
            ImageIcon previewIcon = loadImageIconFromFile(path, PREVIEW_IMAGE_WIDTH, PREVIEW_IMAGE_HEIGHT);
            imagePreviewLabel.setIcon(previewIcon);
            imagePreviewLabel.setText(previewIcon == null ? "Preview N/A" : null);
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Preview");
        }
    }

    private void saveDish() {
        // Get all data from fields and validate it
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String priceStr = priceField.getText().trim().replace(",", ".");
        String ingredients = ingredientsArea.getText().trim();
        String intro = introArea.getText().trim();
        String photoPath = photoPathField.getText().trim();

        if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Type, and Price are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException("Price cannot be negative.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save to database via DAO
        boolean success;
        String successMessage;
        if (isEditMode) {
            dishToEdit.setName(name);
            dishToEdit.setType(type);
            dishToEdit.setPrice(price);
            dishToEdit.setIngredients(ingredients);
            dishToEdit.setIntroduction(intro);
            dishToEdit.setPhotoPath(photoPath);
            success = dishDAO.updateDish(dishToEdit);
            successMessage = "Dish '" + name + "' updated successfully!";
        } else {
            Dish newDish = new Dish(name, type, price, ingredients, intro, photoPath);
            success = dishDAO.addDish(newDish);
            successMessage = "Dish '" + name + "' added successfully!";
        }

        if (success) {
            JOptionPane.showMessageDialog(this, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showSearchDishPanel(); // Go back to the table view
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save dish to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon loadImageIconFromFile(String filePath, int width, int height) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        try {
            File imgFile = new File(filePath);
            if (!imgFile.exists() || !imgFile.isFile()) {
                return null;
            }
            BufferedImage img = ImageIO.read(imgFile);
            if (img == null) {
                return null;
            }

            // Maintain aspect ratio while scaling
            double originalWidth = img.getWidth();
            double originalHeight = img.getHeight();
            double ratio = Math.min((double) width / originalWidth, (double) height / originalHeight);
            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);

            Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            System.err.println("Error loading image from file: " + filePath + " - " + e.getMessage());
            return null;
        }
    }
}