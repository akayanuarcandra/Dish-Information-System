package com.dish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import com.dish.dao.DishDAO;
import com.dish.database.DatabaseConnection;
import com.dish.model.Dish;

public class MenuManager extends JFrame {

    // --- UI Constants (Colors, Fonts, etc.) ---
    // These remain the same as your original file
    private static final Color SIDEBAR_BACKGROUND = new Color(40, 169, 145);
    private static final Color SIDEBAR_TEXT_COLOR = Color.WHITE;
    private static final Color MAIN_CONTENT_BACKGROUND = Color.WHITE;
    private static final Color MAIN_CONTENT_TEXT_COLOR = Color.BLACK;
    private static final Color COLOR_EXIT_BUTTON_BG = new Color(189, 7, 7);
    private static final String ICON_PATH = "/images/logo_white.png";
    private static final String LOGO_PATH = "/images/logo.png";
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font WELCOME_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font TABLE_HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font TABLE_CELL_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final int TABLE_IMAGE_WIDTH = 50;
    private static final int TABLE_IMAGE_HEIGHT = 50;
    private static final int PREVIEW_IMAGE_WIDTH = 100;
    private static final int PREVIEW_IMAGE_HEIGHT = 100;


    // --- UI Components ---
    private final JPanel mainContentPanel;
    private DefaultTableModel dishTableModel;
    private JTable dishTable;
    private TableRowSorter<DefaultTableModel> sorter;
    private final String[] TABLE_COLUMNS = {"ID", "Dish Name", "Dish Type", "Price (¥)", "Ingredients", "Introduction", "Photo", "Actions"};
    private int photoColumnIndex = -1;

    private final DishDAO dishDAO;

    public MenuManager() {
        // *** CORE CHANGE: Initialize the DAO for database operations. ***
        this.dishDAO = new DishDAO();

        setTitle("Dish Information Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // *** CORE CHANGE: Add a listener to close the DB connection when the app closes. ***
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
                System.out.println("Database connection closed.");
            }
        });

        setSize(1440, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        for (int i = 0; i < TABLE_COLUMNS.length; i++) {
            if ("Photo".equals(TABLE_COLUMNS[i])) {
                photoColumnIndex = i;
                break;
            }
        }

        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        this.mainContentPanel = new JPanel(new BorderLayout());
        this.mainContentPanel.setBackground(MAIN_CONTENT_BACKGROUND);
        showWelcomePanel(); // Start with the main dish view
        add(this.mainContentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void clearMainContent() {
        mainContentPanel.removeAll();
    }

    private void refreshMainContent() {
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    
    // UI 

    private void showWelcomePanel() {
        clearMainContent();
        JPanel welcomePanel = createWelcomePanelInternal();
        mainContentPanel.add(welcomePanel, BorderLayout.CENTER);
        refreshMainContent();
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SIDEBAR_BACKGROUND);
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 15, 10, 15);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);
        JLabel logoLabel = new JLabel(loadIconFromClasspath(ICON_PATH, 40, 40));
        headerPanel.add(logoLabel);
        JLabel titleLabel = new JLabel("<html>Dish Information<br>Management</html>");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(SIDEBAR_TEXT_COLOR);
        headerPanel.add(titleLabel);
        gbc.insets = new Insets(40, 15, 30, 15);
        panel.add(headerPanel, gbc);
        gbc.insets = new Insets(10, 15, 10, 15);

        panel.add(createSidebarButton("Search Dishes", "/icons/search.png", e -> showSearchDishPanel()), gbc);
        panel.add(createSidebarButton("Add New Dish", "/icons/add.png", e -> showAddEditDishPanel(null)), gbc);
        panel.add(createSidebarButton("Import Dishes", "/icons/import.png", e -> handleImportDishes()), gbc);
        panel.add(createSidebarButton("Export Dishes", "/icons/export.png", e -> handleExportDishes()), gbc);

        gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc); // Spacer
        gbc.weighty = 0;

        JButton logoutButton = createSidebarButton("Log Out", "/icons/logout.png", e -> {
            this.dispose(); // This will trigger the windowClosing event
            new LoginView().setVisible(true);
        });
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(10, 15, 30, 15);
        panel.add(logoutButton, gbc);

        return panel;
    }

    private JButton createSidebarButton(String text, String iconPath, ActionListener actionListener) {
        JButton button = new JButton(text);
        ImageIcon icon = loadIconFromClasspath(iconPath, 24, 24);
        if (icon != null) {
            button.setIcon(icon);
        }
        button.setFont(DEFAULT_FONT);
        button.setForeground(SIDEBAR_TEXT_COLOR);
        button.setBackground(SIDEBAR_BACKGROUND);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(20);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.addActionListener(actionListener);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setOpaque(true);
                button.setBackground(SIDEBAR_BACKGROUND.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setOpaque(false);
                button.setBackground(SIDEBAR_BACKGROUND);
            }
        });
        return button;
    }

    private JPanel createWelcomePanelInternal() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MAIN_CONTENT_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel mainIconLabel = new JLabel(loadIconFromClasspath(LOGO_PATH, 150, 150));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(mainIconLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome to Dish Information Management, Admin!");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(MAIN_CONTENT_TEXT_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(welcomeLabel, gbc);
        return panel;
    }

    private void showSearchDishPanel() {
        clearMainContent();
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        searchPanel.setBackground(MAIN_CONTENT_BACKGROUND);

        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search by Name/Type: ");
        searchLabel.setFont(DEFAULT_FONT);
        JTextField searchField = new JTextField(30);
        searchField.setFont(DEFAULT_FONT);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(DEFAULT_FONT);

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchInputPanel.setOpaque(false);
        searchInputPanel.add(searchLabel);
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        topPanel.add(searchInputPanel, BorderLayout.WEST);

        JButton clearSearchButton = new JButton("Show All");
        clearSearchButton.setFont(DEFAULT_FONT);
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            if (sorter != null) sorter.setRowFilter(null);
        });
        JPanel clearButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButtonPanel.setOpaque(false);
        clearButtonPanel.add(clearSearchButton);
        topPanel.add(clearButtonPanel, BorderLayout.EAST);

        searchPanel.add(topPanel, BorderLayout.NORTH);

        dishTableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == TABLE_COLUMNS.length - 1; // Only "Actions" column
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == photoColumnIndex) {
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        dishTable = new JTable(dishTableModel);
        dishTable.setFont(TABLE_CELL_FONT);
        dishTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        dishTable.setRowHeight(TABLE_IMAGE_HEIGHT + 10);
        dishTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(dishTableModel);
        dishTable.setRowSorter(sorter);

        ActionListener searchAction = e -> {
            String text = searchField.getText();
            if (text.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                try {
                    List<RowFilter<Object,Object>> rfs = new ArrayList<>(2);
                    rfs.add(RowFilter.regexFilter("(?i)" + text, 1)); // Name column
                    rfs.add(RowFilter.regexFilter("(?i)" + text, 2)); // Type column
                    sorter.setRowFilter(RowFilter.orFilter(rfs));
                } catch (PatternSyntaxException pse) {
                    JOptionPane.showMessageDialog(this, "Invalid search pattern.", "Search Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        TableColumn actionsColumn = dishTable.getColumn("Actions");
        actionsColumn.setCellRenderer(new ActionButtonsRenderer());
        actionsColumn.setCellEditor(new ActionButtonsEditor(dishTable, this));
        actionsColumn.setMinWidth(130);
        actionsColumn.setMaxWidth(150);
        actionsColumn.setPreferredWidth(140);

        if (photoColumnIndex != -1) {
            TableColumn photoCol = dishTable.getColumn(TABLE_COLUMNS[photoColumnIndex]);
            photoCol.setMinWidth(TABLE_IMAGE_WIDTH + 10);
            photoCol.setMaxWidth(TABLE_IMAGE_WIDTH + 50);
            photoCol.setPreferredWidth(TABLE_IMAGE_WIDTH + 50);
        }

        JScrollPane scrollPane = new JScrollPane(dishTable);
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        // *** CORE CHANGE: Populate table with data from the database. ***
        refreshTableData();

        mainContentPanel.add(searchPanel, BorderLayout.CENTER);
        refreshMainContent();
    }
    
    // --- Data Handling Methods (Completely Refactored) ---

    public void refreshTableData() {
        if (dishTableModel == null) return;
        dishTableModel.setRowCount(0);

        // *** CORE CHANGE: Fetch all dishes from the database via the DAO. ***
        List<Dish> dishList = dishDAO.getAllDishes();

        for (Dish dish : dishList) {
            ImageIcon dishImage = loadImageIconFromFile(dish.getPhotoPath(), TABLE_IMAGE_WIDTH, TABLE_IMAGE_HEIGHT);
            dishTableModel.addRow(new Object[]{
                    dish.getId(),
                    dish.getName(),
                    dish.getType(),
                    String.format("%.2f", dish.getPrice()),
                    dish.getIngredients(),
                    dish.getIntroduction(),
                    dishImage,
                    "Actions"
            });
        }
    }

    public void showAddEditDishPanel(Dish dishToEdit) {
        clearMainContent();
        boolean isEditMode = (dishToEdit != null);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(MAIN_CONTENT_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel(isEditMode ? "Edit Dish" : "Add New Dish");
        titleLabel.setFont(TITLE_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(30);
        JTextField typeField = new JTextField(30);
        JTextField priceField = new JTextField(10);
        JTextArea ingredientsArea = new JTextArea(3, 30);
        ingredientsArea.setLineWrap(true); ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsArea);
        JTextArea introArea = new JTextArea(3, 30);
        introArea.setLineWrap(true); introArea.setWrapStyleWord(true);
        JScrollPane introScrollPane = new JScrollPane(introArea);
        JTextField photoPathField = new JTextField(25);
        photoPathField.setEditable(false);
        JButton browseButton = new JButton("Browse...");
        JLabel imagePreviewLabel = new JLabel("No Preview", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(PREVIEW_IMAGE_WIDTH, PREVIEW_IMAGE_HEIGHT));
        imagePreviewLabel.setBorder(BorderFactory.createEtchedBorder());

        if (isEditMode) {
            nameField.setText(dishToEdit.getName());
            typeField.setText(dishToEdit.getType());
            priceField.setText(String.format("%.2f", dishToEdit.getPrice()));
            ingredientsArea.setText(dishToEdit.getIngredients());
            introArea.setText(dishToEdit.getIntroduction());
            photoPathField.setText(dishToEdit.getPhotoPath());
            if (dishToEdit.getPhotoPath() != null && !dishToEdit.getPhotoPath().isEmpty()) {
                ImageIcon currentIcon = loadImageIconFromFile(dishToEdit.getPhotoPath(), PREVIEW_IMAGE_WIDTH, PREVIEW_IMAGE_HEIGHT);
                imagePreviewLabel.setIcon(currentIcon != null ? currentIcon : null);
                imagePreviewLabel.setText(currentIcon == null ? "Preview N/A" : null);
            }
        }

        int y = 1;
        gbc.gridy = y++; gbc.gridx = 0; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; formPanel.add(nameField, gbc); gbc.gridwidth = 1;

        gbc.gridy = y++; gbc.gridx = 0; formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; formPanel.add(typeField, gbc); gbc.gridwidth = 1;

        gbc.gridy = y++; gbc.gridx = 0; formPanel.add(new JLabel("Price (¥):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; formPanel.add(priceField, gbc); gbc.gridwidth = 1;

        gbc.gridy = y++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Ingredients:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        formPanel.add(ingredientsScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0; gbc.weighty = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = y++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Introduction:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        formPanel.add(introScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0; gbc.weighty = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = y; gbc.gridx = 0; formPanel.add(new JLabel("Photo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(photoPathField, gbc); gbc.weightx = 0;
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; formPanel.add(browseButton, gbc);
        y++;

        gbc.gridy = y++; gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(imagePreviewLabel, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Photo File");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                photoPathField.setText(selectedFile.getAbsolutePath());
                ImageIcon previewIcon = loadImageIconFromFile(selectedFile.getAbsolutePath(), PREVIEW_IMAGE_WIDTH, PREVIEW_IMAGE_HEIGHT);
                imagePreviewLabel.setIcon(previewIcon != null ? previewIcon : null);
                imagePreviewLabel.setText(previewIcon == null ? "Preview N/A" : null);
            }
        });

        JButton saveButton = new JButton(isEditMode ? "Save Changes" : "Add Dish");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = y; gbc.gridx = 0; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.1;
        formPanel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
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

            // *** CORE CHANGE: Use DAO to save to database. ***
            if (isEditMode) {
                dishToEdit.setName(name);
                dishToEdit.setType(type);
                dishToEdit.setPrice(price);
                dishToEdit.setIngredients(ingredients);
                dishToEdit.setIntroduction(intro);
                dishToEdit.setPhotoPath(photoPath);
                
                if (dishDAO.updateDish(dishToEdit)) {
                    JOptionPane.showMessageDialog(this, "Dish '" + name + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update dish in the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Dish newDish = new Dish(name, type, price, ingredients, intro, photoPath);
                if (dishDAO.addDish(newDish)) {
                    JOptionPane.showMessageDialog(this, "Dish '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add dish to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            showSearchDishPanel(); // Go back to the main view, which will refresh from DB
        });

        cancelButton.addActionListener(e -> showSearchDishPanel());
        mainContentPanel.add(formPanel, BorderLayout.CENTER);
        refreshMainContent();
    }

    public void deleteDish(int modelRowIndex) {
        // *** CORE CHANGE: Get ID from table model and use DAO to delete. ***
        if (modelRowIndex >= 0 && modelRowIndex < dishTableModel.getRowCount()) {
            int dishId = (int) dishTableModel.getValueAt(modelRowIndex, 0); // ID is in column 0
            String dishName = (String) dishTableModel.getValueAt(modelRowIndex, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + dishName + "' (ID: " + dishId + ")?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dishDAO.deleteDish(dishId)) {
                    JOptionPane.showMessageDialog(this, "Dish '" + dishName + "' deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableData(); // Refresh the table from the database
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete dish from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleImportDishes() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Dishes from TXT");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();
            int importedCount = 0;
            int failedCount = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToImport))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Dish dish = Dish.fromFileString(line);
                    if (dish != null) {
                        // *** CORE CHANGE: Add each dish via the DAO. ***
                        // A more robust implementation might check for existing IDs first.
                        if (dishDAO.addDish(dish)) {
                            importedCount++;
                        } else {
                            failedCount++;
                        }
                    } else {
                        failedCount++;
                    }
                }
                refreshTableData();
                JOptionPane.showMessageDialog(this,
                        "Import complete.\nSuccessfully imported: " + importedCount + " dishes.\nFailed or duplicate lines: " + failedCount,
                        "Import Result", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing file: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void handleExportDishes() {
        // *** CORE CHANGE: Get all dishes from the database first. ***
        List<Dish> allDishes = dishDAO.getAllDishes();
        if (allDishes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No dishes to export.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Dishes to TXT");
        fileChooser.setSelectedFile(new File("dishes_export.txt"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                for (Dish dish : allDishes) {
                    writer.write(dish.toFileString());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Dishes exported successfully to\n" + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // --- Helper & Main Methods (Unchanged) ---

    private ImageIcon loadIconFromClasspath(String path, int width, int height) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            try {
                BufferedImage img = ImageIO.read(imgUrl);
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException e) {
                System.err.println("Couldn't load icon from classpath: " + path + " - " + e.getMessage());
            }
        } else {
            System.err.println("Icon resource not found in classpath: " + path);
        }
        return null;
    }

    private ImageIcon loadImageIconFromFile(String filePath, int width, int height) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        try {
            File imgFile = new File(filePath);
            if (!imgFile.exists() || !imgFile.isFile()) {
                // System.err.println("Image file not found or is not a file: " + filePath);
                return null;
            }
            BufferedImage img = ImageIO.read(imgFile);
            if (img == null) {
                 System.err.println("Could not read image (unsupported format or corrupted file): " + filePath);
                return null;
            }
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            System.err.println("Error loading image from file: " + filePath + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error loading image: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set LookAndFeel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(MenuManager::new);
    }

    // --- Inner classes for JTable Action Buttons ---

    private static class ActionButtonsPanel extends JPanel {
        public final JButton editButton;
        public final JButton deleteButton;

        public ActionButtonsPanel() {
            super(new GridBagLayout());
            setOpaque(true);
            editButton = new JButton("Edit");
            editButton.setBackground(SIDEBAR_BACKGROUND);
            editButton.setForeground(Color.WHITE);
            editButton.setMargin(new Insets(2, 6, 2, 6));
            editButton.setFont(TABLE_CELL_FONT.deriveFont(Font.PLAIN, TABLE_CELL_FONT.getSize() - 1f));

            deleteButton = new JButton("Delete");
            deleteButton.setBackground(COLOR_EXIT_BUTTON_BG);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setMargin(new Insets(2, 6, 2, 6));
            deleteButton.setFont(TABLE_CELL_FONT.deriveFont(Font.PLAIN, TABLE_CELL_FONT.getSize() - 1f));

            GridBagConstraints gbc = new GridBagConstraints();
        
            // --- Add the Edit button ---
            gbc.gridx = 0; // Column 0
            gbc.gridy = 0; // Row 0
            gbc.weightx = 1.0; // Distribute extra horizontal space
            gbc.anchor = GridBagConstraints.LINE_END; // Align to the right side of its cell
            gbc.insets = new Insets(0, 0, 0, 5); // Add 5px of space to its right
            add(editButton, gbc);

            // --- Add the Delete button ---
            gbc.gridx = 1; // Column 1
            gbc.gridy = 0; // Row 0
            gbc.weightx = 1.0; // Distribute extra horizontal space
            gbc.anchor = GridBagConstraints.LINE_START; // Align to the left side of its cell
            gbc.insets = new Insets(0, 5, 0, 0); // Add 5px of space to its left
            add(deleteButton, gbc);
        }
    }

    private static class ActionButtonsRenderer implements TableCellRenderer {
        private final ActionButtonsPanel panel = new ActionButtonsPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : UIManager.getColor("Button.background"));
            return panel;
        }
    }

    private class ActionButtonsEditor extends AbstractCellEditor implements TableCellEditor {
        private final ActionButtonsPanel panel;
        private final JTable table;
        private final MenuManager menuManagerInstance;
        private int currentRowInView;
        private boolean isEditAction;

        public ActionButtonsEditor(JTable table, MenuManager menuManager) {
            this.table = table;
            this.menuManagerInstance = menuManager;
            this.panel = new ActionButtonsPanel();

            this.panel.editButton.addActionListener(e -> {
                isEditAction = true;
                fireEditingStopped();
            });

            this.panel.deleteButton.addActionListener(e -> {
                isEditAction = false;
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRowInView = row;
            panel.setBackground(UIManager.getColor("Button.background"));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            final int modelRow = table.convertRowIndexToModel(currentRowInView);

            SwingUtilities.invokeLater(() -> {
                if (modelRow >= 0 && modelRow < dishTableModel.getRowCount()) {
                    // *** CORE CHANGE: Get ID from table, then fetch the object from the DB. ***
                    int dishId = (int) dishTableModel.getValueAt(modelRow, 0);

                    if (isEditAction) {
                        Dish dishToEdit = menuManagerInstance.dishDAO.getDishById(dishId);
                        if (dishToEdit != null) {
                            menuManagerInstance.showAddEditDishPanel(dishToEdit);
                        } else {
                            JOptionPane.showMessageDialog(menuManagerInstance, "Could not find dish with ID " + dishId + " to edit. It may have been deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                            menuManagerInstance.refreshTableData();
                        }
                    } else {
                        menuManagerInstance.deleteDish(modelRow);
                    }
                }
            });
            return "";
        }
    }
}