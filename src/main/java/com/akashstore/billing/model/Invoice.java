package com.akashstore.billing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {

    private int invoiceId;
    private int customerId;
    private int userId;
    private LocalDateTime invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private String paymentMode;
    private String status; // "ACTIVE", "CANCELLED", "RETURNED"

    public Invoice() {
    }

    public Invoice(int invoiceId, int customerId, int userId, LocalDateTime invoiceDate,
                    BigDecimal totalAmount, BigDecimal discount, String paymentMode, String status) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.userId = userId;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.paymentMode = paymentMode;
        this.status = status;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Invoice [invoiceId=" + invoiceId + ", customerId=" + customerId +
               ", userId=" + userId + ", invoiceDate=" + invoiceDate +
               ", totalAmount=" + totalAmount + ", status=" + status + "]";
    }
}