package com.example.OrderApp.repository;

import com.example.OrderApp.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    List<OrderLine> findOrderLinesByOrder_Id(Long orderId);
    List<OrderLine> findOrderLinesByProduct_Id(Long productId);
    List<OrderLine> findOrderLinesByQuantity(int quantity);
    List<OrderLine> findOrderLinesByQuantityIsBetween(int startQuantity, int endQuantity);
    @Query("SELECT ol FROM OrderLine ol WHERE ol.id BETWEEN :startId AND :endId")
    List<OrderLine> findOrderLinesByIdInRange(Long startId, Long endId);
}
