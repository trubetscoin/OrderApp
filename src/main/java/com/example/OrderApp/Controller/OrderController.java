package com.example.OrderApp.Controller;

import com.example.OrderApp.model.Order;
import com.example.OrderApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/byDate")
    public ResponseEntity<List<Order>> getAllOrdersByDate(@RequestParam("date") Date date) {
        List<Order> orders = orderService.getOrdersBySubmissionDate(date);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/byDateRange")
    public ResponseEntity<List<Order>> getAllOrdersByDate(@RequestParam("startDate") Date startDate, @RequestParam("endDate") Date endDate) {
        List<Order> orders = orderService.getOrdersBySubmissionDate(startDate, endDate);
        return ResponseEntity.ok(orders);
    }
}