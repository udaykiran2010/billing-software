package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.akashstore.billing.model.User;
import com.akashstore.billing.service.AuthService;
import com.akashstore.billing.util.UIStyle;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Billing Software - Login");
        setSize(380, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        UIStyle.styleBackground(mainPanel);

        JLabel titleLabel = new JLabel("Akash Store");
        UIStyle.styleTitleLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 12));
        UIStyle.styleBackground(formPanel);

        JLabel usernameLabel = new JLabel("Username:");
        UIStyle.styleLabel(usernameLabel);
        usernameField = new JTextField();
        UIStyle.styleTextField(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        UIStyle.styleLabel(passwordLabel);
        passwordField = new JPasswordField();
        UIStyle.styleTextField(passwordField);

        JButton loginButton = new JButton("Login");
        UIStyle.styleButton(loginButton);

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel());
        formPanel.add(loginButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton); // pressing Enter triggers login
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(username, password);

        if (user != null) {
            dispose();
            if ("ADMIN".equals(user.getRole())) {
                new AdminDashboardFrame(user).setVisible(true);
            } else {
                new CashierDashboardFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username/password, or account is deactivated",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}