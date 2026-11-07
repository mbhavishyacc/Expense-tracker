package com.bhavishya.expensetracker.data;

import com.bhavishya.expensetracker.model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public final class TransactionRepository {
    public List<Transaction> findAll(long userId) throws SQLException {
        List<Transaction> rows = new ArrayList<>();
        String sql = "SELECT transaction_id, transaction_type, amount, transaction_date, description, category FROM app_transaction WHERE user_id=? ORDER BY transaction_date DESC, transaction_id DESC";
        try (Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); try(ResultSet rs=ps.executeQuery()) { while(rs.next()) rows.add(map(rs)); }
        }
        return rows;
    }
    public void insert(long userId, Transaction t) throws SQLException {
        String category = t instanceof Expense expense ? expense.getCategory().name() : null;
        String sql="INSERT INTO app_transaction(user_id,transaction_type,amount,transaction_date,description,category) VALUES (?,?,?,?,?,?)";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); ps.setString(2,t.getType()); ps.setBigDecimal(3,t.getAmount()); ps.setDate(4,java.sql.Date.valueOf(t.getDate())); ps.setString(5,t.getDescription()); ps.setString(6,category); ps.executeUpdate();
        }
    }
    public boolean update(long userId, long id, Transaction t) throws SQLException {
        String category = t instanceof Expense expense ? expense.getCategory().name() : null;
        String sql="UPDATE app_transaction SET transaction_type=?,amount=?,transaction_date=?,description=?,category=? WHERE transaction_id=? AND user_id=?";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,t.getType()); ps.setBigDecimal(2,t.getAmount()); ps.setDate(3,java.sql.Date.valueOf(t.getDate())); ps.setString(4,t.getDescription()); ps.setString(5,category); ps.setLong(6,id); ps.setLong(7,userId); return ps.executeUpdate()==1;
        }
    }
    public boolean delete(long userId,long id) throws SQLException {
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement("DELETE FROM app_transaction WHERE transaction_id=? AND user_id=?")) {
            ps.setLong(1,id); ps.setLong(2,userId); return ps.executeUpdate()==1;
        }
    }
    public BigDecimal sum(long userId,String type,LocalDate from,LocalDate until) throws SQLException {
        String sql="SELECT COALESCE(SUM(amount),0) FROM app_transaction WHERE user_id=? AND transaction_type=? AND transaction_date>=? AND transaction_date<?";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); ps.setString(2,type); ps.setDate(3,java.sql.Date.valueOf(from)); ps.setDate(4,java.sql.Date.valueOf(until));
            try(ResultSet rs=ps.executeQuery()) { rs.next(); return rs.getBigDecimal(1); }
        }
    }
    public BigDecimal categorySpent(long userId,Category category,LocalDate from,LocalDate until) throws SQLException {
        String sql="SELECT COALESCE(SUM(amount),0) FROM app_transaction WHERE user_id=? AND transaction_type='EXPENSE' AND category=? AND transaction_date>=? AND transaction_date<?";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); ps.setString(2,category.name()); ps.setDate(3,java.sql.Date.valueOf(from)); ps.setDate(4,java.sql.Date.valueOf(until));
            try(ResultSet rs=ps.executeQuery()) { rs.next(); return rs.getBigDecimal(1); }
        }
    }
    public Map<Category,BigDecimal> expensesByCategory(long userId,LocalDate from,LocalDate until) throws SQLException {
        Map<Category,BigDecimal> result=new EnumMap<>(Category.class);
        String sql="SELECT category,COALESCE(SUM(amount),0) FROM app_transaction WHERE user_id=? AND transaction_type='EXPENSE' AND transaction_date>=? AND transaction_date<? GROUP BY category ORDER BY SUM(amount) DESC";
        try(Connection c=Db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,userId); ps.setDate(2,java.sql.Date.valueOf(from)); ps.setDate(3,java.sql.Date.valueOf(until));
            try(ResultSet rs=ps.executeQuery()) { while(rs.next()) result.put(Category.fromLabel(rs.getString(1)),rs.getBigDecimal(2)); }
        }
        return result;
    }
    private Transaction map(ResultSet rs) throws SQLException {
        long id=rs.getLong("transaction_id"); String type=rs.getString("transaction_type"); BigDecimal amount=rs.getBigDecimal("amount");
        LocalDate date=rs.getDate("transaction_date").toLocalDate(); String description=rs.getString("description");
        return "EXPENSE".equals(type) ? new Expense(id,amount,date,description,Category.fromLabel(rs.getString("category"))) : new Income(id,amount,date,description);
    }
}
