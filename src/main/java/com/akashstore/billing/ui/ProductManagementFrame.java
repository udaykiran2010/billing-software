package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
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

import com.akashstore.billing.model.Product;
import com.akashstore.billing.service.ProductService;
import com.akashstore.billing.util.UIStyle;

public class ProductManagementFrame extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductService productService = new ProductService();

    private JTextField nameField, categoryField, priceField, gstField, stockField;

    public ProductManagementFrame() {
        setTitle("Manage Products");
        setSize(680, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("Product Management");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.styleBackground(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        UIStyle.styleBackground(formPanel);

        nameField = new JTextField();
        categoryField = new JTextField();
        priceField = new JTextField();
        gstField = new JTextField();
        stockField = new JTextField();

        JLabel nameLbl = new JLabel("Name:"); UIStyle.styleLabel(nameLbl);
        JLabel categoryLbl = new JLabel("Category:"); UIStyle.styleLabel(categoryLbl);
        JLabel priceLbl = new JLabel("Price:"); UIStyle.styleLabel(priceLbl);
        JLabel gstLbl = new JLabel("GST %:"); UIStyle.styleLabel(gstLbl);
        JLabel stockLbl = new JLabel("Stock Qty:"); UIStyle.styleLabel(stockLbl);

        UIStyle.styleTextField(nameField);
        UIStyle.styleTextField(categoryField);
        UIStyle.styleTextField(priceField);
        UIStyle.styleTextField(gstField);
        UIStyle.styleTextField(stockField);

        JButton addBtn = new JButton("Add Product");
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        UIStyle.styleButton(addBtn);
        UIStyle.styleButton(updateBtn);
        UIStyle.styleDangerButton(deleteBtn);
        UIStyle.styleButton(refreshBtn);

        formPanel.add(nameLbl); formPanel.add(nameField);
        formPanel.add(categoryLbl); formPanel.add(categoryField);
        formPanel.add(priceLbl); formPanel.add(priceField);
        formPanel.add(gstLbl); formPanel.add(gstField);
        formPanel.add(stockLbl); formPanel.add(stockField);
        formPanel.add(addBtn); formPanel.add(deleteBtn);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Category", "Price", "GST %", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        UIStyle.styleTable(productTable);
        centerPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        UIStyle.styleBackground(bottomPanel);
        bottomPanel.add(updateBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        refreshBtn.addActionListener(e -> loadProducts());

        productTable.getSelectionModel().addListSelectionListener(e -> populateFormFromSelection());

        loadProducts();
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getGstPercentage(), p.getStockQuantity()
            });
        }
    }

    private void populateFormFromSelection() {
        int row = productTable.getSelectedRow();
        if (row == -1) return;

        nameField.setText(tableModel.getValueAt(row, 1).toString());
        categoryField.setText(tableModel.getValueAt(row, 2).toString());
        priceField.setText(tableModel.getValueAt(row, 3).toString());
        gstField.setText(tableModel.getValueAt(row, 4).toString());
        stockField.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void handleAdd() {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            BigDecimal price = new BigDecimal(priceField.getText());
            BigDecimal gst = new BigDecimal(gstField.getText());
            int stock = Integer.parseInt(stockField.getText());

            boolean success = productService.addProduct(name, category, price, gst, stock);

            String error = productService.addProductWithError(name, category, price, gst, stock);

            if (error == null) {
                JOptionPane.showMessageDialog(this, "Product added");
                clearForm();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price, GST and Stock must be valid numbers", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleUpdate() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);

            Product product = new Product();
            product.setProductId(id);
            product.setName(nameField.getText());
            product.setCategory(categoryField.getText());
            product.setPrice(new BigDecimal(priceField.getText()));
            product.setGstPercentage(new BigDecimal(gstField.getText()));
            product.setStockQuantity(Integer.parseInt(stockField.getText()));

            boolean success = productService.updateProduct(product);

            String error = productService.updateProductWithError(product);

            if (error == null) {
                JOptionPane.showMessageDialog(this, "Product updated");
                clearForm();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price, GST and Stock must be valid numbers", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = productService.deleteProduct(id);
            if (success) {
                clearForm();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        gstField.setText("");
        stockField.setText("");
    }
}