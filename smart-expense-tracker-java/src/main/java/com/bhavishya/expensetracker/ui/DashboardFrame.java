package com.bhavishya.expensetracker.ui;

import com.bhavishya.expensetracker.data.*;
import com.bhavishya.expensetracker.model.*;
import com.bhavishya.expensetracker.service.TransactionService;
import com.bhavishya.expensetracker.util.Money;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public final class DashboardFrame extends JFrame {
    private final User user; private final TransactionService transactions=new TransactionService(); private final BudgetRepository budgets=new BudgetRepository(); private final ReportRepository reports=new ReportRepository();
    private final DefaultTableModel transactionModel=model("Date","Type","Description","Category","Amount","Record");
    private final JTable transactionTable=new JTable(transactionModel);
    private final DefaultTableModel budgetModel=model("Period","Category","Limit","Spent","Remaining","Status","ID");
    private final JTable budgetTable=new JTable(budgetModel);
    private final JPanel reportPanel=new JPanel(new BorderLayout(8,8));
    private final JLabel incomeTotal=new JLabel(), expenseTotal=new JLabel(), balanceTotal=new JLabel();
    public DashboardFrame(User user) {
        super("Monthly Expense Tracker — "+user.username()); this.user=user; setDefaultCloseOperation(EXIT_ON_CLOSE); setMinimumSize(new Dimension(900,620)); setSize(1080,700); setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout(14,14)); root.setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        JPanel header=new JPanel(new BorderLayout()); JLabel title=new JLabel("Your money, clearly."); title.setFont(title.getFont().deriveFont(Font.BOLD,24f));
        JButton logout=new JButton("Sign out"); logout.addActionListener(e->{dispose();new LoginFrame().setVisible(true);});
        header.add(title,BorderLayout.WEST); header.add(new JLabel("Signed in as  "+user.username()),BorderLayout.CENTER); header.add(logout,BorderLayout.EAST);
        JPanel summary=new JPanel(new GridLayout(1,3,12,0)); summary.add(summaryBox("Income this month",incomeTotal)); summary.add(summaryBox("Expenses this month",expenseTotal)); summary.add(summaryBox("Net balance",balanceTotal));
        JPanel top=new JPanel(new BorderLayout(0,14)); top.add(header,BorderLayout.NORTH); top.add(summary,BorderLayout.SOUTH);
        JTabbedPane tabs=new JTabbedPane(); tabs.addTab("Transactions",transactionsPanel()); tabs.addTab("Budgets",budgetsPanel()); tabs.addTab("Reports",reportPanel);
        root.add(top,BorderLayout.NORTH); root.add(tabs,BorderLayout.CENTER); setContentPane(root);
        refreshAll();
    }
    private JPanel summaryBox(String label,JLabel value) { JPanel p=new JPanel(new BorderLayout(4,4)); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218,223,229)),BorderFactory.createEmptyBorder(12,14,12,14))); JLabel caption=new JLabel(label);caption.setForeground(new Color(90,100,110));value.setFont(value.getFont().deriveFont(Font.BOLD,20f));p.add(caption,BorderLayout.NORTH);p.add(value,BorderLayout.CENTER);return p; }
    private JPanel transactionsPanel() {
        JPanel panel=new JPanel(new BorderLayout(8,12)); JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        JButton add=new JButton("Add transaction"),edit=new JButton("Edit"),delete=new JButton("Delete"),refresh=new JButton("Refresh"); actions.add(add);actions.add(edit);actions.add(delete);actions.add(refresh);
        transactionTable.setFillsViewportHeight(true); transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); transactionTable.removeColumn(transactionTable.getColumnModel().getColumn(5)); panel.add(actions,BorderLayout.NORTH);panel.add(new JScrollPane(transactionTable),BorderLayout.CENTER);
        add.addActionListener(e->editTransaction(null)); edit.addActionListener(e->{Transaction t=selectedTransaction();if(t!=null)editTransaction(t);});
        delete.addActionListener(e->deleteTransaction());refresh.addActionListener(e->refreshAll()); return panel;
    }
    private JPanel budgetsPanel() {
        JPanel panel=new JPanel(new BorderLayout(8,12)); JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        JComboBox<Category> category=new JComboBox<>(Category.values()); JComboBox<Budget.Period> period=new JComboBox<>(Budget.Period.values()); JTextField limit=new JTextField(9); JButton save=new JButton("Save budget"),remove=new JButton("Remove selected");
        controls.add(new JLabel("Category"));controls.add(category);controls.add(new JLabel("Period"));controls.add(period);controls.add(new JLabel("Limit"));controls.add(limit);controls.add(save);controls.add(remove);
        budgetTable.setFillsViewportHeight(true);budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);budgetTable.removeColumn(budgetTable.getColumnModel().getColumn(6));panel.add(controls,BorderLayout.NORTH);panel.add(new JScrollPane(budgetTable),BorderLayout.CENTER);
        save.addActionListener(e->{try{BigDecimal amount=new BigDecimal(limit.getText().trim());if(amount.signum()<=0)throw new IllegalArgumentException();budgets.save(user.userId(),(Category)category.getSelectedItem(),(Budget.Period)period.getSelectedItem(),amount);limit.setText("");refreshBudgets();}catch(Exception ex){showError("Enter a budget amount greater than zero.");}});
        remove.addActionListener(e->{int row=budgetTable.getSelectedRow();if(row<0){showError("Select a budget to remove.");return;}Object id=budgetTable.getValueAt(row,6);try{budgets.delete(user.userId(),Long.parseLong(id.toString()));refreshBudgets();}catch(Exception ex){showError(ex.getMessage());}});
        return panel;
    }
    private void editTransaction(Transaction existing) {
        Transaction value=TransactionDialog.show(this,existing); if(value==null)return;
        try { if(existing==null)transactions.add(user.userId(),value); else transactions.update(user.userId(),existing.getTransactionId(),value); refreshAll(); checkBudgetWarnings(value); }
        catch(Exception ex){showError(ex.getMessage());}
    }
    private void checkBudgetWarnings(Transaction transaction) {
        if(!(transaction instanceof Expense expense))return;
        try { for(Budget budget:budgets.findAll(user.userId())) if(budget.category()==expense.getCategory() && budget.warning()) {
            String state=budget.exceeded()?"You are over":"You have reached 80% of";
            JOptionPane.showMessageDialog(this,state+" your "+budget.period().name().toLowerCase()+" "+budget.category()+" budget ("+Money.format(budget.currentSpent())+" of "+Money.format(budget.limit())+").","Budget alert",JOptionPane.WARNING_MESSAGE); break;
        }} catch(Exception ex){showError(ex.getMessage());}
    }
    private Transaction selectedTransaction() {int row=transactionTable.getSelectedRow();if(row<0){showError("Select a transaction first.");return null;}return (Transaction)transactionModel.getValueAt(row,5);}
    private void deleteTransaction() {Transaction selected=selectedTransaction();if(selected==null)return;if(JOptionPane.showConfirmDialog(this,"Delete this transaction?","Confirm deletion",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;try{transactions.delete(user.userId(),selected.getTransactionId());refreshAll();}catch(Exception ex){showError(ex.getMessage());}}
    private void refreshAll() {refreshTransactions();refreshSummary();refreshBudgets();refreshReports();}
    private void refreshTransactions() {
        transactionModel.setRowCount(0);try{for(Transaction t:transactions.list(user.userId())) {String category=t instanceof Expense x?x.getCategory().toString():"—";transactionModel.addRow(new Object[]{t.getDate(),t.getType(),t.getDescription(),category,Money.format(t.getAmount()),t});}}
        catch(Exception ex){showError(ex.getMessage());}
    }
    private void refreshSummary() {
        LocalDate start=YearMonth.now().atDay(1),end=start.plusMonths(1);try{BigDecimal income=new TransactionRepository().sum(user.userId(),"INCOME",start,end),expense=new TransactionRepository().sum(user.userId(),"EXPENSE",start,end);incomeTotal.setText(Money.format(income));expenseTotal.setText(Money.format(expense));balanceTotal.setText(Money.format(income.subtract(expense)));}
        catch(Exception ex){showError(ex.getMessage());}
    }
    private void refreshBudgets() {
        budgetModel.setRowCount(0);try{for(Budget b:budgets.findAll(user.userId())) {String status=b.exceeded()?"Over limit":b.warning()?"Near limit":"On track";budgetModel.addRow(new Object[]{b.period(),b.category(),Money.format(b.limit()),Money.format(b.currentSpent()),Money.format(b.remaining()),status,b.budgetId()});}}
        catch(Exception ex){showError(ex.getMessage());}
    }
    private void refreshReports() {
        reportPanel.removeAll(); JPanel content=new JPanel(new BorderLayout(12,12)); YearMonth month=YearMonth.now();
        JLabel heading=new JLabel(month.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+month.getYear()+" — spending by category"); heading.setFont(heading.getFont().deriveFont(Font.BOLD,17f));
        DefaultTableModel categoryModel=model("Category","Spent"); JTable categoryTable=new JTable(categoryModel);
        DefaultTableModel trendModel=model("Month","Income","Expenses","Net"); JTable trendTable=new JTable(trendModel);
        try {Map<Category,BigDecimal> values=new TransactionRepository().expensesByCategory(user.userId(),month.atDay(1),month.plusMonths(1).atDay(1));values.forEach((k,v)->categoryModel.addRow(new Object[]{k,Money.format(v)}));
            for(MonthlySummary s:reports.sixMonthTrend(user.userId()))trendModel.addRow(new Object[]{s.month(),Money.format(s.income()),Money.format(s.expenses()),Money.format(s.net())});
        } catch(Exception ex){showError(ex.getMessage());}
        JPanel tables=new JPanel(new GridLayout(1,2,12,0));tables.add(new JScrollPane(categoryTable));tables.add(new JScrollPane(trendTable));content.add(heading,BorderLayout.NORTH);content.add(tables,BorderLayout.CENTER);reportPanel.add(content,BorderLayout.CENTER);reportPanel.revalidate();reportPanel.repaint();
    }
    private void showError(String message){JOptionPane.showMessageDialog(this,message==null?"Unable to complete the request.":message,"Expense Tracker",JOptionPane.WARNING_MESSAGE);}
    private static DefaultTableModel model(String... headers){return new DefaultTableModel(headers,0){@Override public boolean isCellEditable(int row,int column){return false;}};}
}
