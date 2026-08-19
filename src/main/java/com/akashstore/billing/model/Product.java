package com.akashstore.billing.model;

import java.math.BigDecimal;

public class Product {

    private int productId;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal gstPercentage;
    private int stockQuantity;

    public Product() {
    }

    public Product(int productId, String name, String category, BigDecimal price,
                    BigDecimal gstPercentage, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.gstPercentage = gstPercentage;
        this.stockQuantity = stockQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId + ", name=" + name +
               ", category=" + category + ", price=" + price +
               ", gstPercentage=" + gstPercentage + ", stockQuantity=" + stockQuantity + "]";
    }
}