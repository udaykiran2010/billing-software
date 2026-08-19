package com.akashstore.billing.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$"); // Indian mobile numbers: 10 digits, starts 6-9

    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000"); // 10 lakh cap per item
    private static final BigDecimal MAX_GST_PERCENTAGE = new BigDecimal("100");
    private static final int MAX_STOCK_QUANTITY = 100000;
    private static final int MAX_BILL_QUANTITY = 1000;

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return true; // email is optional in our schema, so blank is allowed
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return true; // phone is optional
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPrice(BigDecimal price) {
        if (price == null) return false;
        if (price.compareTo(BigDecimal.ZERO) < 0) return false; // no negatives
        if (price.compareTo(MAX_PRICE) > 0) return false;        // no unrealistic huge prices
        return true;
    }

    public static boolean isValidGstPercentage(BigDecimal gst) {
        if (gst == null) return false;
        if (gst.compareTo(BigDecimal.ZERO) < 0) return false;
        if (gst.compareTo(MAX_GST_PERCENTAGE) > 0) return false; // GST can't exceed 100%
        return true;
    }

    public static boolean isValidStockQuantity(int stock) {
        return stock >= 0 && stock <= MAX_STOCK_QUANTITY;
    }

    public static boolean isValidBillQuantity(int quantity) {
        return quantity > 0 && quantity <= MAX_BILL_QUANTITY; // must bill at least 1, but cap absurd quantities
    }

    public static boolean isValidDiscount(BigDecimal discount, BigDecimal subtotal) {
        if (discount == null) return false;
        if (discount.compareTo(BigDecimal.ZERO) < 0) return false; // no negative discount
        if (discount.compareTo(subtotal) > 0) return false;         // discount can't exceed the bill itself
        return true;
    }

    public static boolean isValidName(String name) {
        return name != null && !name.isBlank() && name.trim().length() <= 100;
    }
}