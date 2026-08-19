package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.akashstore.billing.model.User;
import com.akashstore.billing.service.UserService;
import com.akashstore.billing.util.UIStyle;

public class ManageUsersFrame extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserService userService = new UserService();

    private JTextField newUsernameField;
    private JPasswordField newPasswordField;
    private JComboBox<String> newRoleDropdown;

    public ManageUsersFrame() {
        setTitle("Manage Users");
        setSize(600, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("User Management");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.styleBackground(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        // --- Add User form ---
        JPanel addUserPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        UIStyle.styleBackground(addUserPanel);

        JLabel userLbl = new JLabel("Username:"); UIStyle.styleLabel(userLbl);
        newUsernameField = new JTextField();
        UIStyle.styleTextField(newUsernameField);

        JLabel passLbl = new JLabel("Password:"); UIStyle.styleLabel(passLbl);
        newPasswordField = new JPasswordField();
        UIStyle.styleTextField(newPasswordField);

        JLabel roleLbl = new JLabel("Role:"); UIStyle.styleLabel(roleLbl);
        newRoleDropdown = new JComboBox<>(new String[]{"CASHIER", "ADMIN"});

        JButton addUserBtn = new JButton("Add User");
        UIStyle.styleButton(addUserBtn);

        addUserPanel.add(userLbl); addUserPanel.add(newUsernameField);
        addUserPanel.add(passLbl); addUserPanel.add(newPasswordField);
        addUserPanel.add(roleLbl); addUserPanel.add(newRoleDropdown);
        addUserPanel.add(new JLabel()); addUserPanel.add(addUserBtn);

        centerPanel.add(addUserPanel, BorderLayout.NORTH);

        // --- User table ---
        String[] columns = {"ID", "Username", "Role", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        UIStyle.styleTable(userTable);
        centerPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom buttons ---
        JPanel buttonPanel = new JPanel();
        UIStyle.styleBackground(buttonPanel);
        JButton activateBtn = new JButton("Activate Selected");
        JButton deactivateBtn = new JButton("Deactivate Selected");
        JButton refreshBtn = new JButton("Refresh");

        UIStyle.styleButton(activateBtn);
        UIStyle.styleDangerButton(deactivateBtn);
        UIStyle.styleButton(refreshBtn);

        buttonPanel.add(activateBtn);
        buttonPanel.add(deactivateBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        addUserBtn.addActionListener(e -> handleAddUser());
        activateBtn.addActionListener(e -> updateSelectedUserStatus(true));
        deactivateBtn.addActionListener(e -> updateSelectedUserStatus(false));
        refreshBtn.addActionListener(e -> loadUsers());

        loadUsers();
    }

    private void handleAddUser() {
        String username = newUsernameField.getText();
        String password = new String(newPasswordField.getPassword());
        String role = (String) newRoleDropdown.getSelectedItem();

        String error = userService.createUser(username, password, role);

        if (error == null) {
            JOptionPane.showMessageDialog(this, "User created successfully");
            newUsernameField.setText("");
            newPasswordField.setText("");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                    u.getUserId(), u.getUsername(), u.getRole(), u.isActive() ? "Yes" : "No"
            });
        }
    }

    private void updateSelectedUserStatus(boolean active) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean success = userService.setUserActiveStatus(userId, active);

        if (success) {
            JOptionPane.showMessageDialog(this, "User status updated");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user status", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}