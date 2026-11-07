package com.bhavishya.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Expense extends Transaction {
    private final Category category;
    public Expense(long id, BigDecimal amount, LocalDate date, String description, Category category) {
        super(id, amount, date, description); this.category = category;
    }
    public Category getCategory() { return category; }
    @Override public String getType() { return "EXPENSE"; }
}
