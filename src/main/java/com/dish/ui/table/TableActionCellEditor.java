package com.dish.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;

public class TableActionCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JPanel panel;
    private final JButton editButton;
    private final JButton deleteButton;

    public TableActionCellEditor(JTable table, TableActionListener listener) {
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);

        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        stylizeButton(editButton, new Color(60, 179, 113), new Color(46, 139, 87));
        stylizeButton(deleteButton, new Color(220, 20, 60), new Color(178, 34, 34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel.add(editButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel.add(deleteButton, gbc);

        // --- FINAL, ROBUST ACTION LISTENERS ---
        editButton.addActionListener(e -> {
            // Get the currently edited row's index BEFORE stopping the edit.
            int row = table.getEditingRow();
            
            // Now, tell the table to stop editing. This cleans up the UI.
            fireEditingStopped();
            
            // With the correct row index captured, call the listener.
            if (row != -1 && listener != null) {
                listener.onEdit(row);
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getEditingRow();
            fireEditingStopped();
            if (row != -1 && listener != null) {
                listener.onDelete(row);
            }
        });
    }
    
    private void stylizeButton(JButton button, Color background, Color hover) {
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(background);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // This method just shows the editor component. No state needs to be stored here.
        panel.setBackground(UIManager.getColor("Button.background"));
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}