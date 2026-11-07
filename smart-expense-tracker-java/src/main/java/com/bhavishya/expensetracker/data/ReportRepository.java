package com.bhavishya.expensetracker.data;

import com.bhavishya.expensetracker.model.MonthlySummary;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;

public final class ReportRepository {
    private final TransactionRepository transactions=new TransactionRepository();
    public List<MonthlySummary> sixMonthTrend(long userId) throws SQLException {
        List<MonthlySummary> result=new ArrayList<>(); YearMonth current=YearMonth.now();
        for(int offset=5;offset>=0;offset--) {
            YearMonth month=current.minusMonths(offset); LocalDate start=month.atDay(1), end=start.plusMonths(1);
            BigDecimal income=transactions.sum(userId,"INCOME",start,end), expenses=transactions.sum(userId,"EXPENSE",start,end);
            result.add(new MonthlySummary(month,income,expenses));
        }
        return result;
    }
}
