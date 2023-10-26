package com.example.OrderApp.service;

import com.example.OrderApp.model.Customer;
import com.example.OrderApp.model.Order;
import com.example.OrderApp.model.OrderLine;
import com.example.OrderApp.model.Product;
import com.example.OrderApp.repository.CustomerRepository;
import com.example.OrderApp.repository.OrderLineRepository;
import com.example.OrderApp.repository.OrderRepository;
import com.example.OrderApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Profile("data-init")
public class DataInitializationService {
    private final WebClient webClient;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderRepository orderRepository;


    @Value("${mockaroo.api.key}")
    private String apiKey;

    @Autowired
    public DataInitializationService(WebClient.Builder webClientBuilder, CustomerRepository customerRepository, ProductRepository productRepository, OrderLineRepository orderLineRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderLineRepository = orderLineRepository;
        this.orderRepository = orderRepository;

        this.webClient = webClientBuilder.baseUrl("https://my.api.mockaroo.com").build();

    }

    public List<Customer> generateMockarooCustomers() {
        List<Customer> customers = webClient
                .get()
                .uri("/Customer?key={apiKey}", apiKey)
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new RuntimeException("404 Data not found"))
                )
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.error(new RuntimeException("Failed to fetch data"))
                )
                .bodyToFlux(Customer.class)
                .collectList()
                .block();

        if (customers == null || customers.isEmpty()) {
            throw new RuntimeException("Customers data is not present");
        }
        return customerRepository.saveAll(customers);
    }

    public List<Product> generateMockarooProducts() {
        List<Product> products = webClient
                .get()
                .uri("/Product?key={apiKey}", apiKey)
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new RuntimeException("404 Data not found"))
                )
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.error(new RuntimeException("Failed to fetch data"))
                )
                .bodyToFlux(Product.class)
                .collectList()
                .block();

        if (products == null || products.isEmpty()) {
            throw new RuntimeException("Products data is not present");
        }
        return productRepository.saveAll(products);
    }

    public List<OrderLine> generateMockarooOrderLines() {
        List<Map<String, Object>> orderLinesMaps = webClient
                .get()
                .uri("/OrderLine?key={apiKey}", apiKey)
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new RuntimeException("404 Data not found"))
                )
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.error(new RuntimeException("Failed to fetch data"))
                )
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();

        if (orderLinesMaps == null || orderLinesMaps.isEmpty()) {
            throw new RuntimeException("OrderLines data is not present");
        }

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        for (Map<String, Object> orderLineMap : orderLinesMaps) {
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

    public List<Order> generateMockarooOrders() {
        List<Map<String, Object>> orderMaps = webClient
                .get()
                .uri("/Order?key={apiKey}", apiKey)
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new RuntimeException("404 Data not found"))
                )
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.error(new RuntimeException("Failed to fetch data"))
                )
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();

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
}