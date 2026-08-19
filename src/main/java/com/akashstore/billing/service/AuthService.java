package com.akashstore.billing.service;

import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.akashstore.billing.dao.UserDAO;
import com.akashstore.billing.model.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);

            if (user == null) {
                return null; // no such username
            }

            if (!user.isActive()) {
                return null; // account has been deactivated
            }

            boolean passwordMatches = BCrypt.checkpw(password, user.getPassword());

            if (passwordMatches) {
                return user;
            } else {
                return null; // wrong password
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
   
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        try {
            boolean oldPasswordCorrect = BCrypt.checkpw(oldPassword, user.getPassword());

            if (!oldPasswordCorrect) {
                return false;
            }

            String newHashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            userDAO.updatePassword(user.getUserId(), newHashed);
            user.setPassword(newHashed); // keep the in-memory object in sync too
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}