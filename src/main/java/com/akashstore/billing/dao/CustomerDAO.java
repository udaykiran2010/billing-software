package com.akashstore.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.model.Customer;
import com.akashstore.billing.util.DBConnection;

public class CustomerDAO {

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, phone, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());

            stmt.executeUpdate();
        }
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setInt(4, customer.getCustomerId());

            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        }

        return customers;
    }

    public Customer findById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}