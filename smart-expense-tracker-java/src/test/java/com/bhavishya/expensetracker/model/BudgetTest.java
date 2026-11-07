package com.bhavishya.expensetracker.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {
    @Test void warnsAtEightyPercent() {
        Budget budget = new Budget(1, Category.FOOD, Budget.Period.MONTHLY,
            new BigDecimal("100.00"), new BigDecimal("80.00"), LocalDate.now().withDayOfMonth(1));
        assertTrue(budget.warning());
        assertFalse(budget.exceeded());
    }

    @Test void flagsOverspendingAndRemainingAmount() {
        Budget budget = new Budget(2, Category.RENT, Budget.Period.WEEKLY,
            new BigDecimal("100.00"), new BigDecimal("115.50"), LocalDate.now());
        assertTrue(budget.warning());
        assertTrue(budget.exceeded());
        assertEquals(new BigDecimal("-15.50"), budget.remaining());
    }
}
