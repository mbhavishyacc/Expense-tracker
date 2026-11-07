package com.bhavishya.expensetracker.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}
    public static Connection connect() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/expense_tracker");
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
