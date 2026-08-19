package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.akashstore.billing.model.Customer;
import com.akashstore.billing.service.CustomerService;
import com.akashstore.billing.util.UIStyle;

public class CustomerManagementFrame extends JFrame {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerService customerService = new CustomerService();

    private JTextField nameField, phoneField, emailField;

    public CustomerManagementFrame() {
        setTitle("Manage Customers");
        setSize(620, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("Customer Management");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.styleBackground(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        UIStyle.styleBackground(formPanel);

        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();

        JLabel nameLbl = new JLabel("Name:"); UIStyle.styleLabel(nameLbl);
        JLabel phoneLbl = new JLabel("Phone:"); UIStyle.styleLabel(phoneLbl);
        JLabel emailLbl = new JLabel("Email:"); UIStyle.styleLabel(emailLbl);

        UIStyle.styleTextField(nameField);
        UIStyle.styleTextField(phoneField);
        UIStyle.styleTextField(emailField);

        JButton addBtn = new JButton("Add Customer");
        JButton deleteBtn = new JButton("Delete Selected");
        UIStyle.styleButton(addBtn);
        UIStyle.styleDangerButton(deleteBtn);

        formPanel.add(nameLbl); formPanel.add(nameField);
        formPanel.add(phoneLbl); formPanel.add(phoneField);
        formPanel.add(emailLbl); formPanel.add(emailField);
        formPanel.add(addBtn); formPanel.add(deleteBtn);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        UIStyle.styleTable(customerTable);
        centerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        UIStyle.styleBackground(bottomPanel);
        JButton updateBtn = new JButton("Update Selected");
        JButton refreshBtn = new JButton("Refresh");
        UIStyle.styleButton(updateBtn);
        UIStyle.styleButton(refreshBtn);
        bottomPanel.add(updateBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        refreshBtn.addActionListener(e -> loadCustomers());

        customerTable.getSelectionModel().addListSelectionListener(e -> populateFormFromSelection());

        loadCustomers();
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail()});
        }
    }

    private void populateFormFromSelection() {
        int row = customerTable.getSelectedRow();
        if (row == -1) return;

        nameField.setText(tableModel.getValueAt(row, 1).toString());
        phoneField.setText(tableModel.getValueAt(row, 2) == null ? "" : tableModel.getValueAt(row, 2).toString());
        emailField.setText(tableModel.getValueAt(row, 3) == null ? "" : tableModel.getValueAt(row, 3).toString());
    }

    private void handleAdd() {
        String error = customerService.addCustomer(nameField.getText(), phoneField.getText(), emailField.getText());

        if (error == null) {
            JOptionPane.showMessageDialog(this, "Customer added");
            clearForm();
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    
    }

    private void handleUpdate() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        Customer customer = new Customer();
        customer.setCustomerId(id);
        customer.setName(nameField.getText());
        customer.setPhone(phoneField.getText());
        customer.setEmail(emailField.getText());

        String error = customerService.updateCustomer(customer);

        if (error == null) {
            JOptionPane.showMessageDialog(this, "Customer updated");
            clearForm();
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void handleDelete() {
        int row = customerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = customerService.deleteCustomer(id);
            if (success) {
                clearForm();
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }
}