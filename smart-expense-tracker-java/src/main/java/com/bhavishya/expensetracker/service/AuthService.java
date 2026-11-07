package com.bhavishya.expensetracker.service;

import com.bhavishya.expensetracker.data.UserRepository;
import com.bhavishya.expensetracker.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.util.Optional;

public final class AuthService {
    private final UserRepository users=new UserRepository();
    public User register(String username,String email,char[] password) throws SQLException {
        String cleanName=username.trim(), cleanEmail=email.trim().toLowerCase();
        if(!cleanName.matches("[A-Za-z0-9_]{3,40}")) throw new IllegalArgumentException("Username must be 3–40 letters, numbers, or underscores.");
        if(!cleanEmail.matches("^[^@ ]+@[^@ ]+[.][^@ ]+$")) throw new IllegalArgumentException("Enter a valid email address.");
        if(password.length<8) throw new IllegalArgumentException("Password must contain at least 8 characters.");
        String hash=BCrypt.hashpw(new String(password),BCrypt.gensalt(12));
        try { return users.create(cleanName,cleanEmail,hash); }
        catch(SQLException ex) { if("23505".equals(ex.getSQLState())) throw new IllegalArgumentException("That username or email is already registered."); throw ex; }
    }
    public Optional<User> login(String username,char[] password) throws SQLException {
        Optional<User> candidate=users.findByUsername(username.trim());
        if(candidate.isEmpty()) return Optional.empty();
        return BCrypt.checkpw(new String(password),candidate.get().passwordHash()) ? candidate : Optional.empty();
    }
}
