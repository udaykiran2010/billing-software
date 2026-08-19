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

public class CashierDashboardFrame extends JFrame {

    private User loggedInUser;

    public CashierDashboardFrame(User user) {
        this.loggedInUser = user;

        setTitle("Cashier Dashboard - " + user.getUsername());
        setSize(400, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        UIStyle.styleBackground(mainPanel);

        JLabel titleLabel = new JLabel("Welcome, " + user.getUsername() + " (Cashier)");
        UIStyle.styleTitleLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        UIStyle.styleBackground(buttonPanel);

        JButton billingBtn = new JButton("New Bill");
        JButton changePasswordBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        UIStyle.styleButton(billingBtn);
        UIStyle.styleButton(changePasswordBtn);
        UIStyle.styleDangerButton(logoutBtn);

        buttonPanel.add(billingBtn);
        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        changePasswordBtn.addActionListener(e -> new ChangePasswordFrame(loggedInUser).setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        billingBtn.addActionListener(e -> new BillingFrame(loggedInUser).setVisible(true));
        
    }
}