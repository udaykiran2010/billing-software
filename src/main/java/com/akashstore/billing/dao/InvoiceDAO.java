package com.akashstore.billing.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.akashstore.billing.model.Invoice;
import com.akashstore.billing.model.InvoiceItem;
import com.akashstore.billing.util.DBConnection;

public class InvoiceDAO {

    public int saveInvoiceWithItems(Invoice invoice, List<InvoiceItem> items) throws SQLException {

        String invoiceSql = "INSERT INTO invoices (customer_id, user_id, total_amount, discount, payment_mode, status) VALUES (?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO invoice_items (invoice_id, product_id, quantity, unit_price, gst_amount, line_total) VALUES (?, ?, ?, ?, ?, ?)";
        String stockUpdateSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            int invoiceId;

            // 1. Insert the invoice header, and retrieve its generated ID
            try (PreparedStatement stmt = conn.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS)) {
                if (invoice.getCustomerId() > 0) {
                    stmt.setInt(1, invoice.getCustomerId());
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                stmt.setInt(2, invoice.getUserId());
                stmt.setBigDecimal(3, invoice.getTotalAmount());
                stmt.setBigDecimal(4, invoice.getDiscount());
                stmt.setString(5, invoice.getPaymentMode());
                stmt.setString(6, invoice.getStatus());

                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        invoiceId = keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve generated invoice ID");
                    }
                }
            }

            // 2. Insert each invoice item, and reduce stock for each product
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                 PreparedStatement stockStmt = conn.prepareStatement(stockUpdateSql)) {

                for (InvoiceItem item : items) {
                    itemStmt.setInt(1, invoiceId);
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setBigDecimal(4, item.getUnitPrice());
                    itemStmt.setBigDecimal(5, item.getGstAmount());
                    itemStmt.setBigDecimal(6, item.getLineTotal());
                    itemStmt.executeUpdate();

                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setInt(2, item.getProductId());
                    stockStmt.setInt(3, item.getQuantity());
                    int rowsUpdated = stockStmt.executeUpdate();

                    if (rowsUpdated == 0) {
                        // Not enough stock for this product — abort everything
                        throw new SQLException("Insufficient stock for product ID " + item.getProductId());
                    }
                }
            }

            conn.commit(); // COMMIT — save everything permanently
            return invoiceId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // ROLLBACK — undo everything from this transaction
            }
            throw e;

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // restore default behavior
                conn.close();
            }
        }
    }

    public List<Invoice> findAll() throws SQLException {
        List<Invoice> invoices = new java.util.ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY invoice_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapRow(rs));
            }
        }

        return invoices;
    }

    public void updateStatus(int invoiceId, String status) throws SQLException {
        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, invoiceId);
            stmt.executeUpdate();
        }
    }

    public List<InvoiceItem> findItemsByInvoiceId(int invoiceId) throws SQLException {
        List<InvoiceItem> items = new java.util.ArrayList<>();
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setInvoiceId(rs.getInt("invoice_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setGstAmount(rs.getBigDecimal("gst_amount"));
                    item.setLineTotal(rs.getBigDecimal("line_total"));
                    items.add(item);
                }
            }
        }

        return items;
    }
  
    
    public void cancelInvoiceAndRestoreStock(int invoiceId) throws SQLException {

        String statusSql = "UPDATE invoices SET status = 'CANCELLED' WHERE invoice_id = ? AND status = 'ACTIVE'";
        String getItemsSql = "SELECT product_id, quantity FROM invoice_items WHERE invoice_id = ?";
        String restoreStockSql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Mark invoice as cancelled (only if currently ACTIVE)
            int rowsUpdated;
            try (PreparedStatement stmt = conn.prepareStatement(statusSql)) {
                stmt.setInt(1, invoiceId);
                rowsUpdated = stmt.executeUpdate();
            }

            if (rowsUpdated == 0) {
                throw new SQLException("Invoice not found or already cancelled/returned");
            }

            // 2. Restore stock for every item in this invoice
            try (PreparedStatement getItemsStmt = conn.prepareStatement(getItemsSql)) {
                getItemsStmt.setInt(1, invoiceId);

                try (ResultSet rs = getItemsStmt.executeQuery();
                     PreparedStatement restoreStmt = conn.prepareStatement(restoreStockSql)) {

                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");

                        restoreStmt.setInt(1, quantity);
                        restoreStmt.setInt(2, productId);
                        restoreStmt.executeUpdate();
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;

        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setCustomerId(rs.getInt("customer_id"));
        inv.setUserId(rs.getInt("user_id"));
        java.sql.Timestamp ts = rs.getTimestamp("invoice_date");
        if (ts != null) {
            inv.setInvoiceDate(ts.toLocalDateTime());
        }
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setDiscount(rs.getBigDecimal("discount"));
        inv.setPaymentMode(rs.getString("payment_mode"));
        inv.setStatus(rs.getString("status"));
        return inv;
    }
}