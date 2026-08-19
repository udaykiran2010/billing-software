-- ============================================================
-- Akash Store - Billing Software
-- MySQL Database Schema Script
-- ============================================================

CREATE DATABASE IF NOT EXISTS billing_db;
USE billing_db;

-- ============================================================
-- Table: users
-- Stores login credentials and role for Admin/Cashier accounts
-- ============================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,      -- stores a BCrypt hash, never plain text
    role ENUM('ADMIN', 'CASHIER') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================================
-- Table: customers
-- Stores customer records used when generating invoices
-- ============================================================
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100)
);

-- ============================================================
-- Table: products
-- Stores product catalog with pricing, GST, and stock levels
-- ============================================================
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    gst_percentage DECIMAL(5,2) DEFAULT 0,
    stock_quantity INT NOT NULL DEFAULT 0
);

-- ============================================================
-- Table: invoices
-- Stores the invoice header - one row per bill generated
-- ============================================================
CREATE TABLE invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    user_id INT NOT NULL,
    invoice_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0,
    payment_mode VARCHAR(20),
    status ENUM('ACTIVE','CANCELLED','RETURNED') DEFAULT 'ACTIVE',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ============================================================
-- Table: invoice_items
-- Stores each product line within an invoice (many-to-many
-- link between invoices and products)
-- ============================================================
CREATE TABLE invoice_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    gst_amount DECIMAL(10,2) DEFAULT 0,
    line_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- ============================================================
-- Sample data (optional) - creates one admin login
-- Replace the password hash below with a real BCrypt hash
-- generated via BCrypt.hashpw("yourpassword", BCrypt.gensalt())
-- ============================================================
-- INSERT INTO users (username, password, role, is_active)
-- VALUES ('admin', '<bcrypt-hash-here>', 'ADMIN', TRUE);
