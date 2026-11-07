package com.bhavishya.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Budget(long budgetId, Category category, Period period, BigDecimal limit, BigDecimal currentSpent, LocalDate periodStart) {
    public enum Period { WEEKLY, MONTHLY }
    public BigDecimal remaining() { return limit.subtract(currentSpent); }
    public boolean warning() { return limit.signum() > 0 && currentSpent.compareTo(limit.multiply(new BigDecimal("0.80"))) >= 0; }
    public boolean exceeded() { return currentSpent.compareTo(limit) > 0; }
}
