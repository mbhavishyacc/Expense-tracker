package com.bhavishya.expensetracker.data;

import com.bhavishya.expensetracker.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public final class BudgetRepository {
    public List<Budget> findAll(long userId) throws SQLException {
        List<Budget> budgets=new ArrayList<>(); LocalDate today=LocalDate.now();
        String sql="SELECT budget_id,category,period_type,amount_limit FROM category_budget WHERE user_id=? ORDER BY period_type,category";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); try(ResultSet rs=ps.executeQuery()) {
                while(rs.next()) {
                    Category category=Category.fromLabel(rs.getString("category")); Budget.Period period=Budget.Period.valueOf(rs.getString("period_type"));
                    LocalDate start=period==Budget.Period.MONTHLY ? today.withDayOfMonth(1) : today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate end=period==Budget.Period.MONTHLY ? start.plusMonths(1) : start.plusWeeks(1);
                    BigDecimal spent=new TransactionRepository().categorySpent(userId,category,start,end);
                    budgets.add(new Budget(rs.getLong("budget_id"),category,period,rs.getBigDecimal("amount_limit"),spent,start));
                }
            }
        }
        return budgets;
    }
    public void save(long userId,Category category,Budget.Period period,BigDecimal limit) throws SQLException {
        String sql="INSERT INTO category_budget(user_id,category,period_type,amount_limit) VALUES (?,?,?,?) ON CONFLICT(user_id,category,period_type) DO UPDATE SET amount_limit=EXCLUDED.amount_limit";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); ps.setString(2,category.name()); ps.setString(3,period.name()); ps.setBigDecimal(4,limit); ps.executeUpdate();
        }
    }
    public void delete(long userId,long budgetId) throws SQLException {
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement("DELETE FROM category_budget WHERE budget_id=? AND user_id=?")) { ps.setLong(1,budgetId); ps.setLong(2,userId); ps.executeUpdate(); }
    }
}
