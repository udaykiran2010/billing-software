package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.akashstore.billing.model.Invoice;
import com.akashstore.billing.service.BillingService;
import com.akashstore.billing.dao.InvoiceDAO;
import com.akashstore.billing.util.UIStyle;

public class InvoiceHistoryFrame extends JFrame {

    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private BillingService billingService = new BillingService();
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    public InvoiceHistoryFrame() {
        setTitle("Invoice History");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("Invoice History");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Invoice ID", "Date", "Total", "Discount", "Payment Mode", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceTable = new JTable(tableModel);
        UIStyle.styleTable(invoiceTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        UIStyle.styleBackground(centerPanel);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        centerPanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        UIStyle.styleBackground(bottomPanel);
        JButton cancelBtn = new JButton("Cancel Selected Invoice");
        JButton refreshBtn = new JButton("Refresh");

        UIStyle.styleDangerButton(cancelBtn);
        UIStyle.styleButton(refreshBtn);

        bottomPanel.add(cancelBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> handleCancelInvoice());
        refreshBtn.addActionListener(e -> loadInvoices());

        loadInvoices();
    }

    private void loadInvoices() {
        tableModel.setRowCount(0);
        try {
            List<Invoice> invoices = invoiceDAO.findAll();
            for (Invoice inv : invoices) {
                tableModel.addRow(new Object[]{
                        inv.getInvoiceId(),
                        inv.getInvoiceDate(),
                        inv.getTotalAmount(),
                        inv.getDiscount(),
                        inv.getPaymentMode(),
                        inv.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load invoices: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelInvoice() {
        int row = invoiceTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to cancel");
            return;
        }

        String currentStatus = tableModel.getValueAt(row, 5).toString();
        if (!"ACTIVE".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Only ACTIVE invoices can be cancelled", "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel Invoice #" + invoiceId + "? This will restore stock for all its items.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = billingService.cancelInvoice(invoiceId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Invoice cancelled and stock restored");
                loadInvoices();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel invoice", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}