package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.akashstore.billing.model.User;
import com.akashstore.billing.service.AuthService;
import com.akashstore.billing.util.UIStyle;

public class ChangePasswordFrame extends JFrame {

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private User user;
    private AuthService authService = new AuthService();

    public ChangePasswordFrame(User user) {
        this.user = user;

        setTitle("Change Password");
        setSize(380, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("Change Password");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 12));
        UIStyle.styleBackground(formPanel);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));

        oldPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        JLabel oldLbl = new JLabel("Old Password:"); UIStyle.styleLabel(oldLbl);
        JLabel newLbl = new JLabel("New Password:"); UIStyle.styleLabel(newLbl);
        JLabel confirmLbl = new JLabel("Confirm New:"); UIStyle.styleLabel(confirmLbl);

        UIStyle.styleTextField(oldPasswordField);
        UIStyle.styleTextField(newPasswordField);
        UIStyle.styleTextField(confirmPasswordField);

        JButton submitBtn = new JButton("Update Password");
        UIStyle.styleButton(submitBtn);

        formPanel.add(oldLbl);
        formPanel.add(oldPasswordField);
        formPanel.add(newLbl);
        formPanel.add(newPasswordField);
        formPanel.add(confirmLbl);
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel());
        formPanel.add(submitBtn);

        add(formPanel, BorderLayout.CENTER);

        submitBtn.addActionListener(e -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = authService.changePassword(user, oldPass, newPass);

        if (success) {
            JOptionPane.showMessageDialog(this, "Password updated successfully");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Old password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}