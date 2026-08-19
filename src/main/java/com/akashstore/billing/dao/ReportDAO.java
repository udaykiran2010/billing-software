package com.akashstore.billing.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.util.DBConnection;

public class ReportDAO {

    public BigDecimal getTodaysSales() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM invoices WHERE DATE(invoice_date) = CURDATE() AND status = 'ACTIVE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getMonthSales() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM invoices " +
                     "WHERE MONTH(invoice_date) = MONTH(CURDATE()) AND YEAR(invoice_date) = YEAR(CURDATE()) AND status = 'ACTIVE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }

    public int getTodaysInvoiceCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM invoices WHERE DATE(invoice_date) = CURDATE() AND status = 'ACTIVE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("cnt");
            }
        }
        return 0;
    }

    public List<Object[]> getTopSellingProducts(int limit) throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT p.name, SUM(ii.quantity) AS total_qty " +
                     "FROM invoice_items ii " +
                     "JOIN products p ON ii.product_id = p.product_id " +
                     "JOIN invoices i ON ii.invoice_id = i.invoice_id " +
                     "WHERE i.status = 'ACTIVE' " +
                     "GROUP BY p.product_id, p.name " +
                     "ORDER BY total_qty DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{rs.getString("name"), rs.getInt("total_qty")});
                }
            }
        }

        return results;
    }

    public List<Object[]> getLowStockProducts(int threshold) throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT name, stock_quantity FROM products WHERE stock_quantity <= ? ORDER BY stock_quantity ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{rs.getString("name"), rs.getInt("stock_quantity")});
                }
            }
        }

        return results;
    }
}