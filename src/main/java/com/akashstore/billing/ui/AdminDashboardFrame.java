package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.akashstore.billing.model.User;
import com.akashstore.billing.util.UIStyle;

public class AdminDashboardFrame extends JFrame {

    private User loggedInUser;

    public AdminDashboardFrame(User user) {
        this.loggedInUser = user;

        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(420, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        UIStyle.styleBackground(mainPanel);

        JLabel titleLabel = new JLabel("Welcome, " + user.getUsername() + " (Admin)");
        UIStyle.styleTitleLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 0, 10));
        UIStyle.styleBackground(buttonPanel);

        JButton manageProductsBtn = new JButton("Manage Products");
        JButton manageCustomersBtn = new JButton("Manage Customers");
        JButton manageUsersBtn = new JButton("Manage Users");
        JButton viewReportsBtn = new JButton("Sales Reports");
        JButton billingBtn = new JButton("Billing");
        JButton invoiceHistoryBtn = new JButton("Invoice History");
        JButton changePasswordBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        UIStyle.styleButton(manageProductsBtn);
        UIStyle.styleButton(manageCustomersBtn);
        UIStyle.styleButton(manageUsersBtn);
        UIStyle.styleButton(viewReportsBtn);
        UIStyle.styleButton(billingBtn);
        UIStyle.styleButton(invoiceHistoryBtn);
        UIStyle.styleButton(changePasswordBtn);
        UIStyle.styleDangerButton(logoutBtn);

        buttonPanel.add(manageProductsBtn);
        buttonPanel.add(manageCustomersBtn);
        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(viewReportsBtn);
        buttonPanel.add(billingBtn);
        buttonPanel.add(invoiceHistoryBtn);
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        changePasswordBtn.addActionListener(e -> new ChangePasswordFrame(loggedInUser).setVisible(true));
        manageUsersBtn.addActionListener(e -> new ManageUsersFrame().setVisible(true));
        manageProductsBtn.addActionListener(e -> new ProductManagementFrame().setVisible(true));
        manageCustomersBtn.addActionListener(e -> new CustomerManagementFrame().setVisible(true));
        viewReportsBtn.addActionListener(e -> new ReportsFrame().setVisible(true));
        billingBtn.addActionListener(e -> new BillingFrame(loggedInUser).setVisible(true));
        invoiceHistoryBtn.addActionListener(e -> new InvoiceHistoryFrame().setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}