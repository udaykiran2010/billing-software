package com.akashstore.billing.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.dao.UserDAO;
import com.akashstore.billing.model.User;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            users = userDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean setUserActiveStatus(int userId, boolean active) {
        try {
            userDAO.setActiveStatus(userId, active);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String createUser(String username, String password, String role) {
        if (username == null || username.isBlank()) {
            return "Username is required";
        }
        if (password == null || password.length() < 4) {
            return "Password must be at least 4 characters";
        }
        if (!"ADMIN".equals(role) && !"CASHIER".equals(role)) {
            return "Role must be ADMIN or CASHIER";
        }

        try {
            if (userDAO.usernameExists(username.trim())) {
                return "Username already exists";
            }

            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
            userDAO.createUser(username.trim(), hashedPassword, role);
            return null; // success

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error while creating user";
        }
    }
}