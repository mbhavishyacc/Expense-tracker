package com.bhavishya.expensetracker.data;

import java.sql.Connection;
import java.sql.Statement;

public final class Schema {
    private Schema() {}
    public static void initialize() throws Exception {
        try (Connection connection = Db.connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS app_user (user_id BIGSERIAL PRIMARY KEY, username VARCHAR(40) NOT NULL UNIQUE, email VARCHAR(254) NOT NULL UNIQUE, password_hash VARCHAR(100) NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS app_transaction (transaction_id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE, transaction_type VARCHAR(10) NOT NULL CHECK (transaction_type IN ('EXPENSE','INCOME')), amount NUMERIC(14,2) NOT NULL CHECK (amount > 0), transaction_date DATE NOT NULL, description VARCHAR(240) NOT NULL, category VARCHAR(32), created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_transaction_user_date ON app_transaction(user_id, transaction_date DESC)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS category_budget (budget_id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE, category VARCHAR(32) NOT NULL, period_type VARCHAR(10) NOT NULL CHECK (period_type IN ('WEEKLY','MONTHLY')), amount_limit NUMERIC(14,2) NOT NULL CHECK (amount_limit > 0), UNIQUE(user_id, category, period_type))");
        }
    }
}
