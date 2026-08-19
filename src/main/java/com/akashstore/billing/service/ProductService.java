package com.akashstore.billing.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.dao.ProductDAO;
import com.akashstore.billing.model.Product;
import com.akashstore.billing.util.ValidationUtil;

public class ProductService {

    private ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        try {
            return productDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addProduct(String name, String category, BigDecimal price, BigDecimal gst, int stock) {
        return addProductWithError(name, category, price, gst, stock) == null;
    }

    public String addProductWithError(String name, String category, BigDecimal price, BigDecimal gst, int stock) {
        String error = validateProductFields(name, price, gst, stock);
        if (error != null) {
            return error;
        }

        Product product = new Product();
        product.setName(name.trim());
        product.setCategory(category);
        product.setPrice(price);
        product.setGstPercentage(gst != null ? gst : BigDecimal.ZERO);
        product.setStockQuantity(stock);

        try {
            productDAO.addProduct(product);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error while adding product";
        }
    }

    public String updateProductWithError(Product product) {
        String error = validateProductFields(product.getName(), product.getPrice(), product.getGstPercentage(), product.getStockQuantity());
        if (error != null) {
            return error;
        }

        try {
            productDAO.updateProduct(product);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error while updating product";
        }
    }

    public boolean updateProduct(Product product) {
        return updateProductWithError(product) == null;
    }

    public boolean deleteProduct(int productId) {
        try {
            productDAO.deleteProduct(productId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String validateProductFields(String name, BigDecimal price, BigDecimal gst, int stock) {
        if (!ValidationUtil.isValidName(name)) {
            return "Product name is required and must be under 100 characters";
        }
        if (!ValidationUtil.isValidPrice(price)) {
            return "Price must be zero or positive, and realistic (under 10,00,000)";
        }
        if (!ValidationUtil.isValidGstPercentage(gst)) {
            return "GST percentage must be between 0 and 100";
        }
        if (!ValidationUtil.isValidStockQuantity(stock)) {
            return "Stock quantity must be zero or positive, and under 1,00,000";
        }
        return null;
    }
}