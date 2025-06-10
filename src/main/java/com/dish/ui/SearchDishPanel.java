package com.dish.ui;

import com.dish.dao.DishDAO;
import com.dish.model.Dish;
import com.dish.ui.table.TableActionCellEditor;
import com.dish.ui.table.TableActionCellRenderer;
import com.dish.ui.table.TableActionListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class SearchDishPanel extends JPanel {

    private final DefaultTableModel dishTableModel;
    private final JTable dishTable;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final DishDAO dishDAO;
    private final MenuManager mainFrame; // To communicate back

    private final String[] TABLE_COLUMNS = {"ID", "Dish Name", "Dish Type", "Price (¥)", "Ingredients", "Introduction", "Photo", "Actions"};
    private final int photoColumnIndex = 6;
    private final int TABLE_IMAGE_HEIGHT = 50;

    public SearchDishPanel(DishDAO dishDAO, MenuManager mainFrame) {
        this.dishDAO = dishDAO;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- Top Search Bar ---
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // --- Table ---
        dishTableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == TABLE_COLUMNS.length - 1;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == photoColumnIndex) ? ImageIcon.class : super.getColumnClass(columnIndex);
            }
        };
        dishTable = new JTable(dishTableModel);
        sorter = new TableRowSorter<>(dishTableModel);
        dishTable.setRowSorter(sorter);

        setupTable();

        add(new JScrollPane(dishTable), BorderLayout.CENTER);

        refreshTableData();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search by Name/Type: ");
        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchInputPanel.setOpaque(false);
        searchInputPanel.add(searchLabel);
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        topPanel.add(searchInputPanel, BorderLayout.WEST);

        ActionListener searchAction = e -> {
            String text = searchField.getText();
            if (text.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                try {
                    List<RowFilter<Object, Object>> rfs = new ArrayList<>(2);
                    rfs.add(RowFilter.regexFilter("(?i)" + text, 1)); // Name
                    rfs.add(RowFilter.regexFilter("(?i)" + text, 2)); // Type
                    sorter.setRowFilter(RowFilter.orFilter(rfs));
                } catch (PatternSyntaxException pse) {
                    JOptionPane.showMessageDialog(this, "Invalid search pattern.", "Search Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        JButton clearSearchButton = new JButton("Show All");
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });
        JPanel clearButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButtonPanel.setOpaque(false);
        clearButtonPanel.add(clearSearchButton);
        topPanel.add(clearButtonPanel, BorderLayout.EAST);

        return topPanel;
    }

    private void setupTable() {
        dishTable.setFont(new Font("Arial", Font.PLAIN, 12));
        dishTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        dishTable.setRowHeight(TABLE_IMAGE_HEIGHT + 10);
        dishTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumn actionsColumn = dishTable.getColumn("Actions");
        
        // Use the new listener-based editor/renderer
        TableActionListener listener = new TableActionListener() {
            @Override
            public void onEdit(int row) {
                int modelRow = dishTable.convertRowIndexToModel(row);
                int dishId = (int) dishTable.getValueAt(modelRow, 0);
                mainFrame.showEditDishForm(dishId);
            }

            @Override
            public void onDelete(int row) {
                int modelRow = dishTable.convertRowIndexToModel(row);
                deleteDish(modelRow);
            }
        };
        
        actionsColumn.setCellRenderer(new TableActionCellRenderer());
        actionsColumn.setCellEditor(new TableActionCellEditor(listener));
        actionsColumn.setMinWidth(150);
        actionsColumn.setMaxWidth(180);
        actionsColumn.setPreferredWidth(160);

        if (photoColumnIndex != -1) {
            TableColumn photoCol = dishTable.getColumnModel().getColumn(photoColumnIndex);
            photoCol.setPreferredWidth(TABLE_IMAGE_HEIGHT + 20);
        }
    }

    public void refreshTableData() {
        dishTableModel.setRowCount(0);
        List<Dish> dishList = dishDAO.getAllDishes();
        for (Dish dish : dishList) {
            ImageIcon dishImage = loadImageIconFromFile(dish.getPhotoPath(), 50, 50);
            dishTableModel.addRow(new Object[]{
                dish.getId(), dish.getName(), dish.getType(),
                String.format("%.2f", dish.getPrice()),
                dish.getIngredients(), dish.getIntroduction(),
                dishImage, "Actions"
            });
        }
    }

    private void deleteDish(int modelRowIndex) {
        if (modelRowIndex >= 0) {
            int dishId = (int) dishTableModel.getValueAt(modelRowIndex, 0);
            String dishName = (String) dishTableModel.getValueAt(modelRowIndex, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + dishName + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dishDAO.deleteDish(dishId)) {
                    JOptionPane.showMessageDialog(this, "Dish '" + dishName + "' deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete dish.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private ImageIcon loadImageIconFromFile(String filePath, int width, int height) {
        if (filePath == null || filePath.isEmpty()) {
            return null; // No image path provided
        }
        File imgFile = new File(filePath);
        if (!imgFile.exists()) {
            return null; // File does not exist
        }
        try {
            BufferedImage img = ImageIO.read(imgFile);
            if (img != null) {
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Failed to load image
    }
}