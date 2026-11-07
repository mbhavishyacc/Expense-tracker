package com.bhavishya.expensetracker.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Money {
    private static final NumberFormat FORMAT=NumberFormat.getCurrencyInstance(Locale.US);
    private Money() {}
    public static String format(BigDecimal value) { return FORMAT.format(value==null?BigDecimal.ZERO:value); }
}
