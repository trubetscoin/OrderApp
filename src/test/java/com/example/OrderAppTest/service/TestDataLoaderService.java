package com.example.OrderAppTest.service;

import com.example.OrderApp.model.Customer;
import com.example.OrderApp.model.Order;
import com.example.OrderApp.model.OrderLine;
import com.example.OrderApp.model.Product;
import com.example.OrderApp.repository.CustomerRepository;
import com.example.OrderApp.repository.OrderLineRepository;
import com.example.OrderApp.repository.OrderRepository;
import com.example.OrderApp.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TestDataLoaderService {
    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public TestDataLoaderService(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader, CustomerRepository customerRepository, ProductRepository productRepository, OrderLineRepository orderLineRepository, OrderRepository orderRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderLineRepository = orderLineRepository;
        this.orderRepository = orderRepository;
    }

    public List<Customer> populateCustomers() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:json/Customer.json");
        InputStream inputStream = resource.getInputStream();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Customer> customers = objectMapper.readValue(inputStream, new TypeReference<List<Customer>>() {});

        if (customers == null || customers.isEmpty()) {
            throw new RuntimeException("Customers data is not present");
        }

        return customerRepository.saveAll(customers);
    }

    public List<Product> populateProducts() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:json/Product.json");
        InputStream inputStream = resource.getInputStream();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Product> products = objectMapper.readValue(inputStream, new TypeReference<List<Product>>() {});

        if (products == null || products.isEmpty()) {
            throw new RuntimeException("Products data is not present");
        }

        return productRepository.saveAll(products);
    }

    public List<OrderLine> populateOrderLines() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:json/OrderLine.json");
        InputStream inputStream = resource.getInputStream();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> orderLineMaps = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

        if (orderLineMaps == null || orderLineMaps.isEmpty()) {
            throw new RuntimeException("OrderLines data is not present");
        }

        List<OrderLine> orderLines = new ArrayList<>();
        for (Map<String, Object> orderLineMap : orderLineMaps) {
            OrderLine orderLine = new OrderLine();
            orderLine.setId(Long.valueOf(String.valueOf(orderLineMap.get("id"))));
            orderLine.setQuantity((Integer) orderLineMap.get("quantity"));

            Long orderId = Long.valueOf(String.valueOf(orderLineMap.get("orderId")));
            Order matchingOrder = orderRepository.findById(orderId).orElse(null);

            Long productId = Long.valueOf(String.valueOf(orderLineMap.get("productId")));
            Product matchingProduct = productRepository.findById(productId).orElse(null);

            orderLine.setOrder(matchingOrder);
            orderLine.setProduct(matchingProduct);
            orderLines.add(orderLine);
        }
        return orderLineRepository.saveAll(orderLines);
    }

    public List<Order> populateOrders() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:json/Order.json");
        InputStream inputStream = resource.getInputStream();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> orderMaps = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

        if (orderMaps == null || orderMaps.isEmpty()) {
            throw new RuntimeException("Orders data is not present");
        }

        List<Order> orders = new ArrayList<Order>();
        for (Map<String, Object> orderMap : orderMaps) {
            Order order = new Order();
            order.setId(Long.valueOf(String.valueOf(orderMap.get("id"))));
            String submissionDateStr = (String) orderMap.get("submissionDate");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date submissionDate = new java.sql.Date(sdf.parse(submissionDateStr).getTime());
                order.setSubmissionDate(submissionDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Long customerId = Long.valueOf(String.valueOf(orderMap.get("customerId")));
            Customer matchingCustomer = customerRepository.findById(customerId).orElse(null);

            order.setCustomer(matchingCustomer);
            orders.add(order);
        }
        return orderRepository.saveAll(orders);
    }

    public void resetAutoIncrementForTable(String tableName, Long autoIncrement) {
        String query = "ALTER TABLE " + tableName + " AUTO_INCREMENT = " + autoIncrement;
        jdbcTemplate.execute(query);
    }
}
