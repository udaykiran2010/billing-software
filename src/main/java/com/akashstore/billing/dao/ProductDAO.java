package com.akashstore.billing.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.model.Product;
import com.akashstore.billing.util.DBConnection;

public class ProductDAO {

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, gst_percentage, stock_quantity) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setBigDecimal(4, product.getGstPercentage());
            stmt.setInt(5, product.getStockQuantity());

            stmt.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, gst_percentage = ?, stock_quantity = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setBigDecimal(4, product.getGstPercentage());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setInt(6, product.getProductId());

            stmt.executeUpdate();
        }
    }

    public void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }

        return products;
    }

    public Product findById(int productId) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setGstPercentage(rs.getBigDecimal("gst_percentage"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        return p;
    }
}