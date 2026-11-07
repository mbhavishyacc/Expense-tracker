package com.bhavishya.expensetracker.data;

import com.bhavishya.expensetracker.model.User;
import java.sql.*;
import java.util.Optional;

public final class UserRepository {
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, email, password_hash FROM app_user WHERE lower(username)=lower(?)";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username); try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4))) : Optional.empty();
            }
        }
    }
    public User create(String username, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO app_user(username,email,password_hash) VALUES (?,?,?) RETURNING user_id";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, email); ps.setString(3, passwordHash);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return new User(rs.getLong(1), username, email, passwordHash); }
        }
    }
}
