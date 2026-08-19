package com.akashstore.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.akashstore.billing.model.User;
import com.akashstore.billing.util.DBConnection;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password")); // this is the hashed value
                    user.setRole(rs.getString("role"));
                    user.setActive(rs.getBoolean("is_active"));
                    return user;
                }
            }
        }

        return null; // no user with this username
    }

    public void updatePassword(int userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        }

        return users;
    }
    
    
    
    public void setActiveStatus(int userId, boolean active) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    public void createUser(String username, String hashedPassword, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, is_active) VALUES (?, ?, ?, TRUE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

            stmt.executeUpdate();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if a row was found
            }
        }
    }
}