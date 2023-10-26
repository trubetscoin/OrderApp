package com.example.OrderApp.service;

import com.example.OrderApp.model.Customer;
import com.example.OrderApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long customerID) {
        return customerRepository.findById(customerID).orElse(null);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long customerID, Customer updatedCustomer) {
        if (customerRepository.existsById(customerID)) {
            updatedCustomer.setId(customerID);
            return customerRepository.save(updatedCustomer);
        } else {
            return null;
        }
    }

    public Customer updateCustomer(Customer updatedCustomer) {
        if (customerRepository.existsById(updatedCustomer.getId())) {
            return customerRepository.save(updatedCustomer);
        } else {
            return null;
        }
    }

    public void deleteCustomerById(Long customerID) {
        customerRepository.deleteById(customerID);
    }

    public void deleteCustomersByIdInRange(Long startID, Long endID) {
        List<Customer> customers = customerRepository.findCustomersByIdInRange(startID, endID);
        customerRepository.deleteAll(customers);
    }

}