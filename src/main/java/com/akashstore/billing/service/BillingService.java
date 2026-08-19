package com.akashstore.billing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

import com.akashstore.billing.dao.InvoiceDAO;
import com.akashstore.billing.model.Invoice;
import com.akashstore.billing.model.InvoiceItem;

public class BillingService {

    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    /**
     * Calculates GST amount for a single line: (price * quantity * gstPercent) / 100
     */
    public BigDecimal calculateLineGst(BigDecimal unitPrice, int quantity, BigDecimal gstPercentage) {
        BigDecimal lineBase = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return lineBase.multiply(gstPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates total for a single line: (price * quantity) + gstAmount
     */
    public BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity, BigDecimal gstAmount) {
        BigDecimal lineBase = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return lineBase.add(gstAmount);
    }

    /**
     * Sums all line totals, then subtracts discount.
     */
    public BigDecimal calculateGrandTotal(List<InvoiceItem> items, BigDecimal discount) {
        BigDecimal sum = BigDecimal.ZERO;
        for (InvoiceItem item : items) {
            sum = sum.add(item.getLineTotal());
        }
        if (discount != null) {
            sum = sum.subtract(discount);
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sum = BigDecimal.ZERO; // never allow a negative total
        }
        return sum;
    }

    public int generateInvoice(Invoice invoice, List<InvoiceItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot generate an invoice with no items");
        }
        return invoiceDAO.saveInvoiceWithItems(invoice, items);
    }

    public boolean cancelInvoice(int invoiceId) {
        try {
            invoiceDAO.cancelInvoiceAndRestoreStock(invoiceId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}