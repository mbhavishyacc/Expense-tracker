package com.bhavishya.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Income extends Transaction {
    public Income(long id, BigDecimal amount, LocalDate date, String description) { super(id, amount, date, description); }
    @Override public String getType() { return "INCOME"; }
}
