package com.bhavishya.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Transaction {
    private final long transactionId;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String description;
    protected Transaction(long transactionId, BigDecimal amount, LocalDate date, String description) {
        this.transactionId = transactionId; this.amount = amount; this.date = date; this.description = description;
    }
    public long getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public abstract String getType();
}
