package com.example.OrderApp.service;

import com.example.OrderApp.model.OrderLine;
import com.example.OrderApp.repository.OrderLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderLineService {

    private final OrderLineRepository orderLineRepository;

    @Autowired
    public OrderLineService(OrderLineRepository orderLineRepository) {
        this.orderLineRepository = orderLineRepository;
    }

    public OrderLine createOrderLine(OrderLine orderLine) {
        return orderLineRepository.save(orderLine);
    }

    public OrderLine getOrderLineById(Long orderLineID) {
        return orderLineRepository.findById(orderLineID).orElse(null);
    }

    public List<OrderLine> getOrderLinesByOrderId(Long orderID) {
        return orderLineRepository.findOrderLinesByOrder_Id(orderID);
    }

    public List<OrderLine> getOrderLinesByProductId(Long productID) {
        return orderLineRepository.findOrderLinesByProduct_Id(productID);
    }

    public List<OrderLine> getOrderLinesByQuantity(int quantity) {
        return orderLineRepository.findOrderLinesByQuantity(quantity);
    }

    public List<OrderLine> getOrderLinesByQuantity(int startQuantity, int endQuantity) {
        return orderLineRepository.findOrderLinesByQuantityIsBetween(startQuantity, endQuantity);
    }

    public List<OrderLine> getAllOrderLines() {
        return orderLineRepository.findAll();
    }

    public OrderLine updateOrderLineByQuantity(Long orderLineId, int quantity) {
        Optional<OrderLine> optionalOrderLine = orderLineRepository.findById(orderLineId);
        if (optionalOrderLine.isPresent()) {
            OrderLine orderLine = optionalOrderLine.get();
            orderLine.setQuantity(quantity);
            return orderLineRepository.save(orderLine);
        }
        return null;
    }

    public OrderLine updateOrderLine(Long orderLineID, OrderLine updatedOrderLine) {
        if (orderLineRepository.existsById(orderLineID)) {
            updatedOrderLine.setId(orderLineID);
            return orderLineRepository.save(updatedOrderLine);
        } else {
            return null;
        }
    }

    public OrderLine updateOrderLine(OrderLine updatedOrderLine) {
        if (orderLineRepository.existsById(updatedOrderLine.getId())) {
            return orderLineRepository.save(updatedOrderLine);
        } else {
            return null;
        }
    }

    public void deleteOrderLineById(Long orderLineId) {
        orderLineRepository.deleteById(orderLineId);
    }

    public void deleteOrderLinesByIdInRange(Long startID, Long endID) {
        List<OrderLine> orderLines = orderLineRepository.findOrderLinesByIdInRange(startID, endID);
        orderLineRepository.deleteAll(orderLines);
    }
}