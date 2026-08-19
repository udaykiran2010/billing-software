package com.akashstore.billing.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.akashstore.billing.service.ReportService;
import com.akashstore.billing.util.UIStyle;

public class ReportsFrame extends JFrame {

    private ReportService reportService = new ReportService();

    public ReportsFrame() {
        setTitle("Sales Reports");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        UIStyle.styleBackground(getContentPane());

        JLabel titleLabel = new JLabel("Sales Dashboard");
        UIStyle.styleTitleLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        UIStyle.styleBackground(mainPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        // Summary cards at top
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        UIStyle.styleBackground(summaryPanel);

        JLabel todaySalesLabel = new JLabel("<html>Today's Sales<br><b>₹" + reportService.getTodaysSales() + "</b></html>");
        JLabel monthSalesLabel = new JLabel("<html>This Month<br><b>₹" + reportService.getMonthSales() + "</b></html>");
        JLabel invoiceCountLabel = new JLabel("<html>Today's Bills<br><b>" + reportService.getTodaysInvoiceCount() + "</b></html>");

        UIStyle.styleLabel(todaySalesLabel);
        UIStyle.styleLabel(monthSalesLabel);
        UIStyle.styleLabel(invoiceCountLabel);

        for (JLabel label : new JLabel[]{todaySalesLabel, monthSalesLabel, invoiceCountLabel}) {
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIStyle.PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        }

        summaryPanel.add(todaySalesLabel);
        summaryPanel.add(monthSalesLabel);
        summaryPanel.add(invoiceCountLabel);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Two tables side by side: top-selling products, low stock
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        UIStyle.styleBackground(tablesPanel);

        JPanel topProductsPanel = new JPanel(new BorderLayout());
        UIStyle.styleBackground(topProductsPanel);
        JLabel topProductsLabel = new JLabel("Top Selling Products");
        UIStyle.styleLabel(topProductsLabel);
        topProductsPanel.add(topProductsLabel, BorderLayout.NORTH);

        DefaultTableModel topModel = new DefaultTableModel(new String[]{"Product", "Units Sold"}, 0);
        JTable topTable = new JTable(topModel);
        UIStyle.styleTable(topTable);
        topProductsPanel.add(new JScrollPane(topTable), BorderLayout.CENTER);

        List<Object[]> topProducts = reportService.getTopSellingProducts();
        for (Object[] row : topProducts) {
            topModel.addRow(row);
        }

        JPanel lowStockPanel = new JPanel(new BorderLayout());
        UIStyle.styleBackground(lowStockPanel);
        JLabel lowStockLabel = new JLabel("Low Stock Alert (≤10 units)");
        UIStyle.styleLabel(lowStockLabel);
        lowStockPanel.add(lowStockLabel, BorderLayout.NORTH);

        DefaultTableModel lowStockModel = new DefaultTableModel(new String[]{"Product", "Stock Left"}, 0);
        JTable lowStockTable = new JTable(lowStockModel);
        UIStyle.styleTable(lowStockTable);
        lowStockPanel.add(new JScrollPane(lowStockTable), BorderLayout.CENTER);

        List<Object[]> lowStock = reportService.getLowStockProducts();
        for (Object[] row : lowStock) {
            lowStockModel.addRow(row);
        }

        tablesPanel.add(topProductsPanel);
        tablesPanel.add(lowStockPanel);

        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
}