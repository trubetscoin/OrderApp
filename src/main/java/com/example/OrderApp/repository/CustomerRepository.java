package com.example.OrderApp.repository;

import com.example.OrderApp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.id BETWEEN :startId AND :endId")
    List<Customer> findCustomersByIdInRange(Long startId, Long endId);
}
