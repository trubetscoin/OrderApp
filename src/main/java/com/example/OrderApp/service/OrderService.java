package com.example.OrderApp.service;

import com.example.OrderApp.model.Order;
import com.example.OrderApp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderID) {
        return orderRepository.findById(orderID).orElse(null);
    }

    public List<Order> getOrdersBySubmissionDate(Date date) {
        return orderRepository.findOrdersBySubmissionDate(date);
    }

    public List<Order> getOrdersBySubmissionDate(Date startDate, Date endDate) {
        return orderRepository.findOrdersBySubmissionDateRange(startDate, endDate);
    }

    public List<Order> getOrdersByCustomerId(long customerID) {
        return orderRepository.findOrdersByCustomer_Id(customerID);
    }

    public List<Order> getOrdersByProductName(String productName) {
        return orderRepository.findOrdersByProductName(productName);
    }

    public List<Order> getOrdersByMinTotalPrice(BigDecimal minPrice) {
        return orderRepository.findOrdersByTotalPrice(minPrice);
    }

    public List<Order> getOrdersByProductNameAndMinTotalPrice(String productName, BigDecimal minPrice) {
        return orderRepository.findOrdersByProductNameAndMinTotalPrice(productName, minPrice);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(Long orderID, Order updatedOrder) {
        if (orderRepository.existsById(orderID)) {
            updatedOrder.setId(orderID);
            return orderRepository.save(updatedOrder);
        } else {
            return null;
        }
    }

    public Order updateOrder(Order updatedOrder) {
        if (orderRepository.existsById(updatedOrder.getId())) {
            return orderRepository.save(updatedOrder);
        } else {
            return null;
        }
    }

    public void deleteOrderById(Long orderLineId) {
        orderRepository.deleteById(orderLineId);
    }

    public void deleteOrdersByIdInRange(Long startID, Long endID) {
        List<Order> orders = orderRepository.findOrdersByIdInRange(startID, endID);
        orderRepository.deleteAll(orders);
    }
}