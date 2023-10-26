package com.example.OrderApp.repository;

import com.example.OrderApp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersBySubmissionDate(Date submissionDate);

    @Query("SELECT DISTINCT o FROM Order o " +
            "WHERE o.submissionDate >= :startDate " +
            "AND o.submissionDate <= :endDate ")
    List<Order> findOrdersBySubmissionDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    List<Order> findOrdersByCustomer_Id(Long customerId);

    @Query("SELECT DISTINCT ol.order FROM OrderLine ol " +
            "WHERE ol.product.name = :productName")
    List<Order> findOrdersByProductName(@Param("productName") String productName);

    @Query("SELECT DISTINCT ol.order FROM OrderLine ol " +
            "WHERE ol.product.unitPrice * (CAST(ol.quantity AS BigDecimal)) >= :minTotalPrice")
    List<Order> findOrdersByTotalPrice(
            @Param("minTotalPrice") BigDecimal minTotalPrice
    );

    @Query("SELECT DISTINCT ol.order FROM OrderLine ol " +
            "WHERE ol.product.name = :productName " +
            "AND ol.product.unitPrice * (CAST(ol.quantity AS BigDecimal)) >= :minTotalPrice")
    List<Order> findOrdersByProductNameAndMinTotalPrice(
            @Param("productName") String productName,
            @Param("minTotalPrice") BigDecimal minTotalPrice
    );

    @Query("SELECT o FROM Order o WHERE o.id BETWEEN :startId AND :endId")
    List<Order> findOrdersByIdInRange(Long startId, Long endId);
}