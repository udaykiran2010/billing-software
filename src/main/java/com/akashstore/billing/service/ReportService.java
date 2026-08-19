package com.akashstore.billing.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.dao.ReportDAO;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    public BigDecimal getTodaysSales() {
        try {
            return reportDAO.getTodaysSales();
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getMonthSales() {
        try {
            return reportDAO.getMonthSales();
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public int getTodaysInvoiceCount() {
        try {
            return reportDAO.getTodaysInvoiceCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Object[]> getTopSellingProducts() {
        try {
            return reportDAO.getTopSellingProducts(5);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Object[]> getLowStockProducts() {
        try {
            return reportDAO.getLowStockProducts(10);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}