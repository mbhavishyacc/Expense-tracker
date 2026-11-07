package com.bhavishya.expensetracker.service;

import com.bhavishya.expensetracker.data.TransactionRepository;
import com.bhavishya.expensetracker.model.Transaction;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class TransactionService {
    private final TransactionRepository repository=new TransactionRepository();
    public List<Transaction> list(long userId) throws SQLException { return repository.findAll(userId); }
    public void add(long userId,Transaction transaction) throws SQLException { validate(transaction); repository.insert(userId,transaction); }
    public boolean update(long userId,long id,Transaction transaction) throws SQLException { validate(transaction); return repository.update(userId,id,transaction); }
    public boolean delete(long userId,long id) throws SQLException { return repository.delete(userId,id); }
    private void validate(Transaction transaction) {
        if(transaction.getAmount()==null || transaction.getAmount().signum()<=0) throw new IllegalArgumentException("Amount must be greater than zero.");
        if(transaction.getDate()==null || transaction.getDate().isAfter(LocalDate.now())) throw new IllegalArgumentException("Choose a valid date that is not in the future.");
        if(transaction.getDescription()==null || transaction.getDescription().isBlank() || transaction.getDescription().length()>240) throw new IllegalArgumentException("Description is required and must be 240 characters or fewer.");
    }
}
