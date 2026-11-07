package com.bhavishya.expensetracker.ui;

import com.bhavishya.expensetracker.model.User;
import com.bhavishya.expensetracker.service.AuthService;
import javax.swing.*;
import java.awt.*;

public final class LoginFrame extends JFrame {
    private final AuthService auth=new AuthService();
    private final JTextField username=new JTextField(22);
    private final JPasswordField password=new JPasswordField(22);
    public LoginFrame() {
        super("Monthly Expense Tracker"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(440,330); setLocationRelativeTo(null); setResizable(false);
        JPanel outer=new JPanel(new BorderLayout(16,16)); outer.setBorder(BorderFactory.createEmptyBorder(28,34,26,34));
        JLabel title=new JLabel("Monthly Expense Tracker",SwingConstants.CENTER); title.setFont(title.getFont().deriveFont(Font.BOLD,22f));
        JLabel subtitle=new JLabel("Sign in to your personal ledger",SwingConstants.CENTER); subtitle.setForeground(new Color(90,100,110));
        JPanel heading=new JPanel(new GridLayout(2,1,4,4)); heading.add(title); heading.add(subtitle);
        JPanel form=new JPanel(new GridLayout(2,2,10,12)); form.add(new JLabel("Username")); form.add(username); form.add(new JLabel("Password")); form.add(password);
        JButton signIn=new JButton("Sign in"), register=new JButton("Create account");
        JPanel actions=new JPanel(new GridLayout(1,2,10,0)); actions.add(signIn); actions.add(register);
        outer.add(heading,BorderLayout.NORTH); outer.add(form,BorderLayout.CENTER); outer.add(actions,BorderLayout.SOUTH); setContentPane(outer);
        signIn.addActionListener(e->login()); register.addActionListener(e->register()); getRootPane().setDefaultButton(signIn);
    }
    private void login() {
        try {
            var result=auth.login(username.getText(),password.getPassword());
            if(result.isEmpty()) { JOptionPane.showMessageDialog(this,"Username or password is incorrect.","Sign in",JOptionPane.WARNING_MESSAGE); return; }
            openDashboard(result.get());
        } catch(Exception ex) { showError(ex); }
    }
    private void register() {
        JTextField name=new JTextField(20), email=new JTextField(20); JPasswordField pass=new JPasswordField(20), confirm=new JPasswordField(20);
        JPanel panel=new JPanel(new GridLayout(4,2,8,8)); panel.add(new JLabel("Username"));panel.add(name);panel.add(new JLabel("Email"));panel.add(email);panel.add(new JLabel("Password (8+ characters)"));panel.add(pass);panel.add(new JLabel("Confirm password"));panel.add(confirm);
        if(JOptionPane.showConfirmDialog(this,panel,"Create your account",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return;
        char[] p=pass.getPassword(), c=confirm.getPassword();
        if(!java.util.Arrays.equals(p,c)){JOptionPane.showMessageDialog(this,"Passwords do not match.","Registration",JOptionPane.WARNING_MESSAGE);return;}
        try { User user=auth.register(name.getText(),email.getText(),p); JOptionPane.showMessageDialog(this,"Account created. You are signed in.","Welcome",JOptionPane.INFORMATION_MESSAGE); openDashboard(user); }
        catch(Exception ex){showError(ex);} finally {java.util.Arrays.fill(p,'\0');java.util.Arrays.fill(c,'\0');}
    }
    private void openDashboard(User user) { dispose(); new DashboardFrame(user).setVisible(true); }
    private void showError(Exception ex) { JOptionPane.showMessageDialog(this,ex.getMessage()==null?"Unable to complete the request.":ex.getMessage(),"Something went wrong",JOptionPane.ERROR_MESSAGE); }
}
