// Filename: App.java
package com.dish;

import javax.swing.SwingUtilities;

import com.dish.ui.LoginView;
import com.formdev.flatlaf.FlatLightLaf;

public class App {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            LoginView loginFrame = new LoginView();
            loginFrame.setVisible(true);
        });
    }
}