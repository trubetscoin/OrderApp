package com.example.OrderApp.repository;

import com.example.OrderApp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.id BETWEEN :startId AND :endId")
    List<Product> findProductsByIdInRange(Long startId, Long endId);
}
