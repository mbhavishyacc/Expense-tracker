package com.bhavishya.expensetracker.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySummary(YearMonth month, BigDecimal income, BigDecimal expenses) {
    public BigDecimal net() { return income.subtract(expenses); }
}
