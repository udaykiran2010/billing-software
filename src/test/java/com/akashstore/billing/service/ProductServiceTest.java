package com.akashstore.billing.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class ProductServiceTest {

    private ProductService productService = new ProductService();

    @Test
    void testAddProductFailsWithBlankName() {
        boolean result = productService.addProduct("", "Category", new BigDecimal("100"), new BigDecimal("5"), 10);
        assertFalse(result, "Product with blank name should not be added");
    }

    @Test
    void testAddProductFailsWithNegativePrice() {
        boolean result = productService.addProduct("Test Item", "Category", new BigDecimal("-50"), new BigDecimal("5"), 10);
        assertFalse(result, "Product with negative price should not be added");
    }

    @Test
    void testAddProductFailsWithNegativeStock() {
        boolean result = productService.addProduct("Test Item", "Category", new BigDecimal("100"), new BigDecimal("5"), -5);
        assertFalse(result, "Product with negative stock should not be added");
    }

    @Test
    void testAddProductSucceedsWithValidData() {
        boolean result = productService.addProduct("JUnit Test Product", "TestCategory", new BigDecimal("50.00"), new BigDecimal("5.00"), 20);
        assertTrue(result, "Product with valid data should be added successfully");
    }
}