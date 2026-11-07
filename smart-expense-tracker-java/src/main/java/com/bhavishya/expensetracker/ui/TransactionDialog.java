package com.bhavishya.expensetracker.ui;

import com.bhavishya.expensetracker.model.*;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

final class TransactionDialog {
    private TransactionDialog() {}
    static Transaction show(Component parent, Transaction existing) {
        JComboBox<String> type=new JComboBox<>(new String[]{"Expense","Income"});
        JTextField amount=new JTextField(existing==null?"":existing.getAmount().toPlainString(),18);
        JTextField date=new JTextField(existing==null?LocalDate.now().toString():existing.getDate().toString(),18);
        JTextField description=new JTextField(existing==null?"":existing.getDescription(),18);
        JComboBox<Category> category=new JComboBox<>(Category.values());
        if(existing instanceof Expense expense){type.setSelectedItem("Expense");category.setSelectedItem(expense.getCategory());} else if(existing instanceof Income) type.setSelectedItem("Income");
        JPanel panel=new JPanel(new GridLayout(5,2,8,8)); panel.add(new JLabel("Type"));panel.add(type);panel.add(new JLabel("Amount"));panel.add(amount);panel.add(new JLabel("Date (YYYY-MM-DD)"));panel.add(date);panel.add(new JLabel("Description"));panel.add(description);panel.add(new JLabel("Category (expenses)"));panel.add(category);
        while(JOptionPane.showConfirmDialog(parent,panel,existing==null?"Add transaction":"Edit transaction",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION) {
            try {
                BigDecimal value=new BigDecimal(amount.getText().trim()); LocalDate day=LocalDate.parse(date.getText().trim());
                return "Expense".equals(type.getSelectedItem())?new Expense(existing==null?0:existing.getTransactionId(),value,day,description.getText().trim(),(Category)category.getSelectedItem()):new Income(existing==null?0:existing.getTransactionId(),value,day,description.getText().trim());
            } catch(NumberFormatException|DateTimeParseException ex) { JOptionPane.showMessageDialog(parent,"Enter a valid amount and date.","Check your entries",JOptionPane.WARNING_MESSAGE); }
        }
        return null;
    }
}
