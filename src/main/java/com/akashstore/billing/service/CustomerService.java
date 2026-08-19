package com.akashstore.billing.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.akashstore.billing.dao.CustomerDAO;
import com.akashstore.billing.model.Customer;
import com.akashstore.billing.util.ValidationUtil;

public class CustomerService {

    private CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String addCustomer(String name, String phone, String email) {
        String error = validateCustomerFields(name, phone, email);
        if (error != null) {
            return error;
        }

        Customer customer = new Customer();
        customer.setName(name.trim());
        customer.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        customer.setEmail(email == null || email.isBlank() ? null : email.trim());

        try {
            customerDAO.addCustomer(customer);
            return null; // null means success
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error while adding customer";
        }
    }

    public String updateCustomer(Customer customer) {
        String error = validateCustomerFields(customer.getName(), customer.getPhone(), customer.getEmail());
        if (error != null) {
            return error;
        }

        try {
            customerDAO.updateCustomer(customer);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error while updating customer";
        }
    }

    public boolean deleteCustomer(int customerId) {
        try {
            customerDAO.deleteCustomer(customerId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String validateCustomerFields(String name, String phone, String email) {
        if (!ValidationUtil.isValidName(name)) {
            return "Name is required and must be under 100 characters";
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return "Phone number must be a valid 10-digit mobile number";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Email format is invalid";
        }
        return null; // no errors
    }
}