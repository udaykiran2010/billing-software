package com.akashstore.billing.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

public class UIStyle {

    public static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    public static final Color PRIMARY_DARK = new Color(25, 65, 190);
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    public static final Color TEXT_COLOR = new Color(33, 37, 41);
    public static final Color DANGER_COLOR = new Color(220, 53, 69);

    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    public static void styleButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(DANGER_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    public static void styleLabel(JLabel label) {
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_COLOR);
    }

    public static void styleTitleLabel(JLabel label) {
        label.setFont(FONT_TITLE);
        label.setForeground(PRIMARY_DARK);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_LABEL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_LABEL);
        table.setRowHeight(26);
        table.setSelectionBackground(new Color(220, 230, 255));
        table.setSelectionForeground(TEXT_COLOR);
        table.setGridColor(new Color(222, 226, 230));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BUTTON);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
    }

    public static void styleBackground(Component component) {
        component.setBackground(BACKGROUND_COLOR);
    }
}