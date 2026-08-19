package com.akashstore.billing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.akashstore.billing.model.Invoice;
import com.akashstore.billing.model.InvoiceItem;
import com.akashstore.billing.model.Product;

public class BillingServiceTest {

    private BillingService billingService = new BillingService();
    private ProductService productService = new ProductService();

    // ---------- Pure calculation tests ----------

    @Test
    void testCalculateLineGst() {
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal gstPercentage = new BigDecimal("5.00");

        BigDecimal expectedGst = new BigDecimal("10.00"); // (100 * 2 * 5) / 100 = 10

        BigDecimal actualGst = billingService.calculateLineGst(unitPrice, quantity, gstPercentage);

        assertEquals(0, expectedGst.compareTo(actualGst));
    }

    @Test
    void testCalculateLineTotal() {
        BigDecimal unitPrice = new BigDecimal("100.00");
        int quantity = 2;
        BigDecimal gstAmount = new BigDecimal("10.00");

        BigDecimal expectedTotal = new BigDecimal("210.00"); // (100*2) + 10

        BigDecimal actualTotal = billingService.calculateLineTotal(unitPrice, quantity, gstAmount);

        assertEquals(0, expectedTotal.compareTo(actualTotal));
    }

    @Test
    void testCalculateGrandTotalWithDiscount() {
        List<InvoiceItem> items = new ArrayList<>();

        InvoiceItem item1 = new InvoiceItem();
        item1.setLineTotal(new BigDecimal("210.00"));
        items.add(item1);

        InvoiceItem item2 = new InvoiceItem();
        item2.setLineTotal(new BigDecimal("100.00"));
        items.add(item2);

        BigDecimal discount = new BigDecimal("50.00");

        BigDecimal expectedGrandTotal = new BigDecimal("260.00"); // 210 + 100 - 50

        BigDecimal actualGrandTotal = billingService.calculateGrandTotal(items, discount);

        assertEquals(0, expectedGrandTotal.compareTo(actualGrandTotal));
    }

    @Test
    void testCalculateGrandTotalNeverGoesNegative() {
        List<InvoiceItem> items = new ArrayList<>();

        InvoiceItem item1 = new InvoiceItem();
        item1.setLineTotal(new BigDecimal("50.00"));
        items.add(item1);

        BigDecimal discount = new BigDecimal("100.00"); // discount bigger than total

        BigDecimal actualGrandTotal = billingService.calculateGrandTotal(items, discount);

        assertEquals(0, BigDecimal.ZERO.compareTo(actualGrandTotal));
    }

    @Test
    void testGenerateInvoiceThrowsExceptionForEmptyCart() {
        List<InvoiceItem> emptyItems = new ArrayList<>();

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> billingService.generateInvoice(new Invoice(), emptyItems)
        );

        assertEquals("Cannot generate an invoice with no items", exception.getMessage());
    }

    // ---------- Edge case / integration tests (use real DB) ----------

    @Test
    void testInsufficientStockThrowsException() {
        productService.addProduct("JUnit Low Stock Item", "Test", new BigDecimal("50.00"), new BigDecimal("5.00"), 1);

        List<Product> products = productService.getAllProducts();
        Product testProduct = products.get(products.size() - 1); // last one added

        InvoiceItem item = new InvoiceItem();
        item.setProductId(testProduct.getProductId());
        item.setQuantity(5); // requesting more than the 1 in stock
        item.setUnitPrice(testProduct.getPrice());
        item.setGstAmount(BigDecimal.ZERO);
        item.setLineTotal(testProduct.getPrice().multiply(BigDecimal.valueOf(5)));

        List<InvoiceItem> items = new ArrayList<>();
        items.add(item);

        Invoice invoice = new Invoice();
        invoice.setUserId(1); // assumes admin user_id = 1 exists
        invoice.setTotalAmount(item.getLineTotal());
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setPaymentMode("CASH");
        invoice.setStatus("ACTIVE");

        Exception exception = assertThrows(
                SQLException.class,
                () -> billingService.generateInvoice(invoice, items)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testCancellingAlreadyCancelledInvoiceFails() throws Exception {
        productService.addProduct("JUnit Cancel Test Item", "Test", new BigDecimal("20.00"), new BigDecimal("0.00"), 10);

        List<Product> products = productService.getAllProducts();
        Product testProduct = products.get(products.size() - 1);

        InvoiceItem item = new InvoiceItem();
        item.setProductId(testProduct.getProductId());
        item.setQuantity(2);
        item.setUnitPrice(testProduct.getPrice());
        item.setGstAmount(BigDecimal.ZERO);
        item.setLineTotal(testProduct.getPrice().multiply(BigDecimal.valueOf(2)));

        List<InvoiceItem> items = new ArrayList<>();
        items.add(item);

        Invoice invoice = new Invoice();
        invoice.setUserId(1);
        invoice.setTotalAmount(item.getLineTotal());
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setPaymentMode("CASH");
        invoice.setStatus("ACTIVE");

        int invoiceId = billingService.generateInvoice(invoice, items);

        boolean firstCancel = billingService.cancelInvoice(invoiceId);
        assertEquals(true, firstCancel, "First cancellation should succeed");

        boolean secondCancel = billingService.cancelInvoice(invoiceId);
        assertEquals(false, secondCancel, "Cancelling an already-cancelled invoice should fail");
    }
}