package com.bhavishya.expensetracker;

import com.bhavishya.expensetracker.data.Schema;
import com.bhavishya.expensetracker.ui.LoginFrame;
import javax.swing.*;

public final class App {
    private App() {}
    public static void main(String[] args) {
        try {
            Schema.initialize();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                "Could not connect to the database. Check PostgreSQL and DB_URL, DB_USER, and DB_PASSWORD.\n\n" + ex.getMessage(),
                "Database connection", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new LoginFrame().setVisible(true);
        });
    }
}
