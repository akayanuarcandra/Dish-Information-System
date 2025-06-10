package com.dish.ui;

import com.dish.dao.DishDAO;
import com.dish.database.DatabaseConnection;
import com.dish.model.Dish;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.List;

public class MenuManager extends JFrame {

    // --- UI Constants ---
    private static final Color SIDEBAR_BACKGROUND = new Color(40, 169, 145);
    private static final Color SIDEBAR_TEXT_COLOR = Color.WHITE;
    private static final String ICON_PATH = "/images/logo_white.png";
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);

    // --- Core Components ---
    private final JPanel mainContentPanel;
    private final DishDAO dishDAO;

    // --- Panels managed by CardLayout ---
    private final WelcomePanel welcomePanel;
    private final SearchDishPanel searchDishPanel;
    // DishFormPanel is created on-the-fly since it can be for adding or editing specific dishes

    public MenuManager() {
        this.dishDAO = new DishDAO();

        setTitle("Dish Information Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1440, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Add a listener to close the DB connection when the app closes.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
                System.out.println("Database connection closed.");
            }
        });

        // --- Sidebar ---
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // --- Main Content Area ---
        // CardLayout is perfect for swapping between different full-screen panels
        mainContentPanel = new JPanel(new CardLayout());
        add(mainContentPanel, BorderLayout.CENTER);

        // --- Create Panel Instances ---
        welcomePanel = new WelcomePanel();
        searchDishPanel = new SearchDishPanel(dishDAO, this);

        // Add the primary panels to the CardLayout with unique names
        mainContentPanel.add(welcomePanel, "WELCOME");
        mainContentPanel.add(searchDishPanel, "SEARCH");

        // Start by showing the welcome panel
        showWelcomePanel();
        setVisible(true);
    }

    // --- Panel Switching Methods ---
    // These methods are called by the sidebar buttons or other panels

    public void showWelcomePanel() {
        CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
        cl.show(mainContentPanel, "WELCOME");
    }

    public void showSearchDishPanel() {
        // Always refresh the table data when switching to this panel
        searchDishPanel.refreshTableData();
        CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
        cl.show(mainContentPanel, "SEARCH");
    }

    public void showAddDishForm() {
        // Create a new form panel for adding a dish (dish is null)
        DishFormPanel addForm = new DishFormPanel(dishDAO, this, null);
        mainContentPanel.add(addForm, "ADD_FORM"); // Add it to the card layout
        CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
        cl.show(mainContentPanel, "ADD_FORM"); // Switch to it
    }

    public void showEditDishForm(int dishId) {
        Dish dishToEdit = dishDAO.getDishById(dishId);
        if (dishToEdit != null) {
            // Create a new form panel pre-populated with the dish to edit
            DishFormPanel editForm = new DishFormPanel(dishDAO, this, dishToEdit);
            String cardName = "EDIT_FORM_" + dishId; // Unique name for this card
            mainContentPanel.add(editForm, cardName);
            CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
            cl.show(mainContentPanel, cardName);
        } else {
            JOptionPane.showMessageDialog(this, "Could not find the dish to edit. It may have been deleted by another user.", "Error", JOptionPane.ERROR_MESSAGE);
            showSearchDishPanel(); // Go back to the search view
        }
    }

    // --- Sidebar Creation ---
    // This logic remains here as it's part of the main frame's structure

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

        // Header
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

        // Buttons that switch panels
        panel.add(createSidebarButton("Home", "/icons/home.png", e -> showWelcomePanel()), gbc);
        panel.add(createSidebarButton("Search Dishes", "/icons/search.png", e -> showSearchDishPanel()), gbc);
        panel.add(createSidebarButton("Add New Dish", "/icons/add.png", e -> showAddDishForm()), gbc);
        panel.add(createSidebarButton("Import Dishes", "/icons/import.png", e -> handleImportDishes()), gbc);
        panel.add(createSidebarButton("Export Dishes", "/icons/export.png", e -> handleExportDishes()), gbc);

        // Spacer to push logout button to the bottom
        gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        gbc.weighty = 0;

        // Logout Button
        JButton logoutButton = createSidebarButton("Log Out", "/icons/logout.png", e -> {
            this.dispose();
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
    
    // --- Import/Export Logic ---
    // This can stay here or be moved to a dedicated utility class if it grows more complex.
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
                        if (dishDAO.addDish(dish)) {
                            importedCount++;
                        } else {
                            failedCount++;
                        }
                    } else {
                        failedCount++;
                    }
                }
                showSearchDishPanel(); // Refresh view after import
                JOptionPane.showMessageDialog(this,
                        "Import complete.\nSuccessfully imported: " + importedCount + " dishes.\nFailed or duplicate lines: " + failedCount,
                        "Import Result", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing file: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportDishes() {
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
            }
        }
    }

    // --- Helper Methods ---
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

    // --- Main Method ---
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set LookAndFeel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(MenuManager::new);
    }
}