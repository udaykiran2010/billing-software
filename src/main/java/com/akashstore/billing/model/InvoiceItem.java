package com.akashstore.billing.model;

import java.math.BigDecimal;

public class InvoiceItem {

    private int itemId;
    private int invoiceId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal gstAmount;
    private BigDecimal lineTotal;

    public InvoiceItem() {
    }

    public InvoiceItem(int itemId, int invoiceId, int productId, int quantity,
                        BigDecimal unitPrice, BigDecimal gstAmount, BigDecimal lineTotal) {
        this.itemId = itemId;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.gstAmount = gstAmount;
        this.lineTotal = lineTotal;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    @Override
    public String toString() {
        return "InvoiceItem [itemId=" + itemId + ", invoiceId=" + invoiceId +
               ", productId=" + productId + ", quantity=" + quantity +
               ", lineTotal=" + lineTotal + "]";
    }
}