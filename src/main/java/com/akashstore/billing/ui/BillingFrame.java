package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.akashstore.billing.model.Customer;
import com.akashstore.billing.model.Invoice;
import com.akashstore.billing.model.InvoiceItem;
import com.akashstore.billing.model.Product;
import com.akashstore.billing.model.User;
import com.akashstore.billing.service.BillingService;
import com.akashstore.billing.service.CustomerService;
import com.akashstore.billing.service.ProductService;
import com.akashstore.billing.util.UIStyle;

public class BillingFrame extends JFrame {

    private User cashier;
    private ProductService productService = new ProductService();
    private CustomerService customerService = new CustomerService();
    private BillingService billingService = new BillingService();

    private JComboBox<Product> productDropdown;
    private JComboBox<Customer> customerDropdown;
    private JTextField quantityField;
    private JTextField discountField;
    private JLabel totalLabel;
    private JComboBox<String> paymentModeDropdown;

    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    private List<InvoiceItem> cartItems = new ArrayList<>();
    private List<Product> productList;

    public BillingFrame(User cashier) {
        this.cashier = cashier;

        setTitle("New Bill - " + cashier.getUsername());
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("New Bill");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.styleBackground(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        JPanel topForm = new JPanel(new GridLayout(2, 4, 10, 10));
        UIStyle.styleBackground(topForm);

        JLabel customerLbl = new JLabel("Customer:"); UIStyle.styleLabel(customerLbl);
        customerDropdown = new JComboBox<>();

        JLabel productLbl = new JLabel("Product:"); UIStyle.styleLabel(productLbl);
        productDropdown = new JComboBox<>();

        JLabel qtyLbl = new JLabel("Quantity:"); UIStyle.styleLabel(qtyLbl);
        quantityField = new JTextField();
        UIStyle.styleTextField(quantityField);

        JButton addToCartBtn = new JButton("Add to Cart");
        UIStyle.styleButton(addToCartBtn);

        topForm.add(customerLbl); topForm.add(customerDropdown);
        topForm.add(productLbl); topForm.add(productDropdown);
        topForm.add(qtyLbl); topForm.add(quantityField);
        topForm.add(new JLabel()); topForm.add(addToCartBtn);

        centerPanel.add(topForm, BorderLayout.NORTH);

        String[] columns = {"Product", "Qty", "Unit Price", "GST", "Line Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        UIStyle.styleTable(cartTable);
        centerPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomForm = new JPanel(new GridLayout(2, 4, 10, 10));
        UIStyle.styleBackground(bottomForm);

        JLabel discountLbl = new JLabel("Discount:"); UIStyle.styleLabel(discountLbl);
        discountField = new JTextField("0");
        UIStyle.styleTextField(discountField);

        JLabel paymentLbl = new JLabel("Payment Mode:"); UIStyle.styleLabel(paymentLbl);
        paymentModeDropdown = new JComboBox<>(new String[]{"CASH", "CARD", "UPI"});

        totalLabel = new JLabel("Total: 0.00");
        UIStyle.styleLabel(totalLabel);

        JButton generateBtn = new JButton("Generate Invoice");
        UIStyle.styleButton(generateBtn);

        bottomForm.add(discountLbl); bottomForm.add(discountField);
        bottomForm.add(paymentLbl); bottomForm.add(paymentModeDropdown);
        bottomForm.add(totalLabel); bottomForm.add(generateBtn);

        centerPanel.add(bottomForm, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        addToCartBtn.addActionListener(e -> handleAddToCart());
        generateBtn.addActionListener(e -> handleGenerateInvoice());
        discountField.addActionListener(e -> refreshTotal());

        loadDropdowns();
        setupDropdownRenderers();
    }

    private void loadDropdowns() {
        productList = productService.getAllProducts();
        productDropdown.removeAllItems();
        for (Product p : productList) {
            productDropdown.addItem(p);
        }

        customerDropdown.removeAllItems();
        customerDropdown.addItem(null);
        for (Customer c : customerService.getAllCustomers()) {
            customerDropdown.addItem(c);
        }
    }

    private void setupDropdownRenderers() {
        productDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                Product p = (Product) value;
                label.setText(p.getName() + " - Rs." + p.getPrice() + " (Stock: " + p.getStockQuantity() + ")");
            }
            return label;
        });

        customerDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value == null) {
                label.setText("Walk-in Customer");
            } else {
                Customer c = (Customer) value;
                label.setText(c.getName() + (c.getPhone() != null ? " (" + c.getPhone() + ")" : ""));
            }
            return label;
        });
    }

    private void handleAddToCart() {
        Product selectedProduct = (Product) productDropdown.getSelectedItem();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select a product");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!com.akashstore.billing.util.ValidationUtil.isValidBillQuantity(quantity)) {
            JOptionPane.showMessageDialog(this, "Quantity must be between 1 and 1000", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (quantity > selectedProduct.getStockQuantity()) {
            JOptionPane.showMessageDialog(this, "Only " + selectedProduct.getStockQuantity() + " in stock", "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal gstAmount = billingService.calculateLineGst(selectedProduct.getPrice(), quantity, selectedProduct.getGstPercentage());
        BigDecimal lineTotal = billingService.calculateLineTotal(selectedProduct.getPrice(), quantity, gstAmount);

        InvoiceItem item = new InvoiceItem();
        item.setProductId(selectedProduct.getProductId());
        item.setQuantity(quantity);
        item.setUnitPrice(selectedProduct.getPrice());
        item.setGstAmount(gstAmount);
        item.setLineTotal(lineTotal);

        cartItems.add(item);
        cartTableModel.addRow(new Object[]{
                selectedProduct.getName(), quantity, selectedProduct.getPrice(), gstAmount, lineTotal
        });

        quantityField.setText("");
        refreshTotal();
    }

    private void refreshTotal() {
        BigDecimal discount;
        try {
            discount = new BigDecimal(discountField.getText());
        } catch (NumberFormatException e) {
            discount = BigDecimal.ZERO;
        }

        BigDecimal total = billingService.calculateGrandTotal(cartItems, discount);
        totalLabel.setText("Total: " + total);
    }

    private void handleGenerateInvoice() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add at least one product.");
            return;
        }

        BigDecimal discount;
        try {
            discount = new BigDecimal(discountField.getText());
        } catch (NumberFormatException e) {
            discount = BigDecimal.ZERO;
        }

        BigDecimal grandTotal = billingService.calculateGrandTotal(cartItems, discount);
        
        BigDecimal subtotal = billingService.calculateGrandTotal(cartItems, BigDecimal.ZERO);

        if (!com.akashstore.billing.util.ValidationUtil.isValidDiscount(discount, subtotal)) {
            JOptionPane.showMessageDialog(this, "Discount cannot be negative or exceed the bill subtotal", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Invoice invoice = new Invoice();
        Customer selectedCustomer = (Customer) customerDropdown.getSelectedItem();
        invoice.setCustomerId(selectedCustomer != null ? selectedCustomer.getCustomerId() : 0);
        invoice.setUserId(cashier.getUserId());
        invoice.setTotalAmount(grandTotal);
        invoice.setDiscount(discount);
        invoice.setPaymentMode((String) paymentModeDropdown.getSelectedItem());
        invoice.setStatus("ACTIVE");

        try {
            int invoiceId = billingService.generateInvoice(invoice, cartItems);
            JOptionPane.showMessageDialog(this, "Invoice #" + invoiceId + " generated successfully!\nTotal: " + grandTotal);

            cartItems.clear();
            cartTableModel.setRowCount(0);
            discountField.setText("0");
            totalLabel.setText("Total: 0.00");
            loadDropdowns();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to generate invoice: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}