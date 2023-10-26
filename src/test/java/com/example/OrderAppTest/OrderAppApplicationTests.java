package com.example.OrderAppTest;

import com.example.OrderApp.model.Order;
import com.example.OrderApp.model.OrderLine;
import com.example.OrderApp.model.Product;
import com.example.OrderAppTest.config.TestConfig;
import com.example.OrderApp.model.Customer;
import com.example.OrderApp.service.*;
import com.example.OrderAppTest.service.TestDataLoaderService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderAppApplicationTests {
	private final TestDataLoaderService testDataLoaderService;
	private final CustomerService customerService;
	private final ProductService productService;
	private final OrderLineService orderLineService;
	private final OrderService orderService;

	@Autowired
	OrderAppApplicationTests(TestDataLoaderService testDataLoaderService, CustomerService customerService, ProductService productService, OrderLineService orderLineService, OrderService orderService) {
		this.testDataLoaderService = testDataLoaderService;
		this.customerService = customerService;
		this.productService = productService;
		this.orderLineService = orderLineService;
		this.orderService = orderService;
	}

	@BeforeAll
	public void initializeTestData() throws IOException {
		testDataLoaderService.populateCustomers();
		testDataLoaderService.populateProducts();
		testDataLoaderService.populateOrders();
		testDataLoaderService.populateOrderLines();
	}

	@Test
	public void testCustomerCreate() {
		Customer customer = new Customer();
		customer.setRegistrationCode("223523523");
		customer.setFullName("zxc cxz");
		customer.setEmail("zxc@example.com");
		customer.setTelephone("+1234567890");

		Customer savedCustomer = customerService.createCustomer(customer);

		assertNotNull(savedCustomer.getId());
		assertEquals("223523523", savedCustomer.getRegistrationCode());
		assertEquals("zxc cxz", savedCustomer.getFullName());
		assertEquals("zxc@example.com", savedCustomer.getEmail());
		assertEquals("+1234567890", savedCustomer.getTelephone());
	}

	@Test
	public void testCustomerGetById() {
		//{"id":3,"registrationCode":"419-10-0113","fullName":"Cam Love","email":"clove2@hostgator.com","telephone":"+48 769 381 6496"}
		Long customerId = 3L;
		Customer retrievedCustomer = customerService.getCustomerById(customerId);

		assertNotNull(retrievedCustomer);
		assertEquals("419-10-0113", retrievedCustomer.getRegistrationCode());
		assertEquals("Cam Love", retrievedCustomer.getFullName());
		assertEquals("clove2@hostgator.com", retrievedCustomer.getEmail());
		assertEquals("+48 769 381 6496", retrievedCustomer.getTelephone());
	}

	@Test
	public void testCustomersGetAll() {
		List<Customer> allCustomers = customerService.getAllCustomers();
		assertEquals(50, allCustomers.size());
	}

	@Test
	public void testCustomerUpdate() {
		//{"id":5,"registrationCode":"540-84-0677","fullName":"Jayne Covert","email":"jcovert4@spiegel.de","telephone":"+62 314 795 2210"}
		Customer retrievedCustomer = customerService.getCustomerById(5L);

		retrievedCustomer.setFullName("Updated Full Name");

		Customer updatedCustomer = customerService.updateCustomer(retrievedCustomer);

		assertEquals("Updated Full Name", updatedCustomer.getFullName());
	}

	@Test
	public void testCustomerDeleteById() {
		Customer customer = new Customer();
		customer.setRegistrationCode("45745744");
		customer.setFullName("QWE EWQ");
		customer.setEmail("QWE@example.com");
		customer.setTelephone("+5251231123");

		Customer savedCustomer = customerService.createCustomer(customer);

		customerService.deleteCustomerById(savedCustomer.getId());

		assertNull(customerService.getCustomerById(savedCustomer.getId()));
	}

	@Test
	public void testCustomersDeleteByIdInRange() {
		customerService.deleteCustomersByIdInRange(1L, 10L);

		List<Customer> remainingCustomers = customerService.getAllCustomers();

		assertEquals(40, remainingCustomers.size());
	}

	@Test
	public void testProductCreate() {
		Product product = new Product();

		product.setName("TEST PRODUCT");
		product.setSkuCode("12354123512");
		product.setUnitPrice(new BigDecimal("10.99"));

		Product createdProduct = productService.createProduct(product);
		Product retrievedProduct = productService.getProductById(createdProduct.getId());

		assertEquals(product, retrievedProduct);
	}

	@Test
	public void testProductGetById() {
		//{"id":7,"name":"Nut - Walnut, Pieces","skuCode":"8121234980","unitPrice":82.01}
		Product retrievedProduct = productService.getProductById(7L);

		assertEquals("Nut - Walnut, Pieces" , retrievedProduct.getName());
		assertEquals("8121234980" , retrievedProduct.getSkuCode());
		assertEquals(new BigDecimal("82.01"), retrievedProduct.getUnitPrice());
	}

	@Test
	public void testProductsGetAll() {
		List<Product> allProducts = productService.getAllProducts();
		assertEquals(50, allProducts.size());
	}

	@Test
	public void testProductUpdate() {
		//{"id":9,"name":"Coffee - Hazelnut Cream","skuCode":"9111335157","unitPrice":75.96}
		Product retrievedProduct = productService.getProductById(9L);

		retrievedProduct.setName("ZXCZXCZXCZXC");
		retrievedProduct.setUnitPrice(new BigDecimal("23.43"));

		Product updatedProduct = productService.updateProduct(retrievedProduct);

		assertEquals("ZXCZXCZXCZXC", updatedProduct.getName());
		assertEquals(new BigDecimal("23.43"), updatedProduct.getUnitPrice());
	}

	@Test
	public void testProductDeleteById() {
		Product product = new Product();
		product.setName("gsdfgsfd");
		product.setSkuCode("23462346");
		product.setUnitPrice(new BigDecimal("43.12"));

		Product createdProduct = productService.createProduct(product);

		productService.deleteProductById(createdProduct.getId());

		assertNull(productService.getProductById(createdProduct.getId()));
	}

	@Test
	public void testProductsDeleteByIdInRange() {
		productService.deleteProductsByIdInRange(1L, 10L);

		List<Product> remainingProducts = productService.getAllProducts();

		assertEquals(40, remainingProducts.size());
	}

	@Test
	public void testOrderCreate() {
		Order order = new Order();
		order.setSubmissionDate(new Date(System.currentTimeMillis()));
		order.setCustomer(customerService.getCustomerById(1L));

		Order createdOrder = orderService.createOrder(order);

		Order retrievedOrder = orderService.getOrderById(createdOrder.getId());

		assertEquals(order, retrievedOrder);
	}

	@Test
	public void testOrderGetById() {
		//{"id":10,"submissionDate":"2023-05-17","customerId":42}
		Order retrievedOrder = orderService.getOrderById(10L);

		Date submissionDate = retrievedOrder.getSubmissionDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		assertEquals("2023-05-17", sdf.format(submissionDate));
		assertEquals(42L, retrievedOrder.getCustomer().getId());
	}

	@Test
	public void testOrderGetBySubmissionDate() throws ParseException {
		//{"id":10,"submissionDate":"2023-05-17","customerId":42}
		String submissionDateString = "2023-05-17";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date submissionDate = new Date(sdf.parse(submissionDateString).getTime());

		List<Order> retrievedOrders = orderService.getOrdersBySubmissionDate(submissionDate);
		// it works as long as in the Order.json there is ONLY ONE order with
		// submission date equals to "2023-05-17"
		assertEquals(1, retrievedOrders.size());

		Order retrievedOrder = retrievedOrders.get(0);

		assertEquals("2023-05-17", sdf.format(retrievedOrder.getSubmissionDate()));
		assertEquals(42L, retrievedOrder.getCustomer().getId());
		assertEquals(1, retrievedOrders.size());
	}

	@Test
	public void testOrderGetByCustomerId() throws ParseException {
		//{"id":10,"submissionDate":"2023-05-17","customerId":42}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<Order> retrievedOrders = orderService.getOrdersByCustomerId(42);
		// it works as long as in the Order.json there are ONLY THREE order with
		// submission customerId equals to 42
		assertEquals(3, retrievedOrders.size());

		Order retrievedOrder = retrievedOrders.get(0);

		assertEquals("2023-05-17", sdf.format(retrievedOrder.getSubmissionDate()));
		assertEquals(42L, retrievedOrder.getCustomer().getId());
	}

	@Test
	public void testOrderGetByProductName() throws ParseException {
		//{"id":6,"name":"Beef - Flank Steak","skuCode":"4196832040","unitPrice":48.47}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String productName = "Beef - Flank Steak";

		List<Order> retrievedOrders = orderService.getOrdersByProductName(productName);
		// Order has no clue about Product. So, there is a helper here
		List<OrderLine> retrievedOrderLines = orderLineService.getOrderLinesByProductId(6L);

		// it works as long as in the Order.json there are ONLY TWO order with
		// product name equals to "Beef - Flank Steak"
		assertEquals(2, retrievedOrders.size());
		assertEquals(2, retrievedOrderLines.size());

		// assert that these two list's data is the same
		assertEquals(retrievedOrders.get(0).getId(), retrievedOrderLines.get(0).getOrder().getId());
		assertEquals(retrievedOrders.get(1).getId(), retrievedOrderLines.get(1).getOrder().getId());

		assertEquals("Beef - Flank Steak", retrievedOrderLines.get(0).getProduct().getName());
		assertEquals("Beef - Flank Steak", retrievedOrderLines.get(1).getProduct().getName());
	}

	@Test
	public void testOrdersGetAll() {
		List<Order> allOrders = orderService.getAllOrders();
		assertEquals(50, allOrders.size());
	}

	@Test
	public void testUpdateOrder() throws ParseException {
		//{"id":12,"submissionDate":"2023-03-17","customerId":15}
		Order retrievedOrder = orderService.getOrderById(12L);

		String updatedSubmissionDateString = "2023-05-20";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date updatedSubmissionDate = new Date(sdf.parse(updatedSubmissionDateString).getTime());

		retrievedOrder.setSubmissionDate(updatedSubmissionDate);
		retrievedOrder.setCustomer(customerService.getCustomerById(4L));

		Order updatedOrder = orderService.updateOrder(retrievedOrder);

		Date submissionDate = updatedOrder.getSubmissionDate();

		assertEquals("2023-05-20", sdf.format(submissionDate));
		assertEquals(4L, updatedOrder.getCustomer().getId());
	}

	@Test
	public void testOrderDeleteById() {
		Order order = new Order();
		order.setSubmissionDate(new Date(System.currentTimeMillis()));
		order.setCustomer(customerService.getCustomerById(1L));

		Order createdOrder = orderService.createOrder(order);

		orderService.deleteOrderById(createdOrder.getId());

		assertNull(orderService.getOrderById(createdOrder.getId()));
	}

	@Test
	public void testOrdersDeleteByIdInRange() {
		orderService.deleteOrdersByIdInRange(1L, 10L);

		List<Order> remainingOrders = orderService.getAllOrders();

		assertEquals(40, remainingOrders.size());
	}

	@Test
	public void testCreateOrderLine() {
		OrderLine orderLine = new OrderLine();
		Order retrievedOrder = orderService.getOrderById(1L);
		Product retrievedProduct = productService.getProductById(1L);

		orderLine.setQuantity(5);
		orderLine.setOrder(retrievedOrder);
		orderLine.setProduct(retrievedProduct);

		OrderLine createdOrderLine = orderLineService.createOrderLine(orderLine);

		OrderLine retrievedOrderLine = orderLineService.getOrderLineById(createdOrderLine.getId());

		assertEquals(createdOrderLine, retrievedOrderLine);
	}

	@Test
	public void testGetOrderLineById() {
		//{"id":11,"orderId":33,"productId":48,"quantity":42}

		OrderLine retrievedOrderLine = orderLineService.getOrderLineById(11L);

		assertEquals(42, retrievedOrderLine.getQuantity());
		assertEquals(33L, retrievedOrderLine.getOrder().getId());
		assertEquals(48L, retrievedOrderLine.getProduct().getId());
	}

	@Test
	public void testGetOrderLinesByOrderId() {
		//orderline: {"id":23,"orderId":37,"productId":8,"quantity":98}
		//order: {"id":37,"submissionDate":"2023-08-15","customerId":16}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<OrderLine> retrievedOrderLines = orderLineService.getOrderLinesByOrderId(37L);
		// it works as long as in the OrderLine.json there are ONLY TWO orderlines with
		// orderId equals to 37
		assertEquals(2, retrievedOrderLines.size());

		assertEquals(37L, retrievedOrderLines.get(0).getOrder().getId());
		assertEquals(8L, retrievedOrderLines.get(0).getProduct().getId());
		assertEquals("2023-08-15", sdf.format(retrievedOrderLines.get(0).getOrder().getSubmissionDate()));
		assertEquals(98, retrievedOrderLines.get(0).getQuantity());
	}

	@Test
	public void testGetOrderLinesByProductId() {
		//orderline: {"id":12,"orderId":14,"productId":20,"quantity":85}
		//product: {"id":20,"name":"Chilli Paste, Sambal Oelek","skuCode":"9400728700","unitPrice":56.12}

		List<OrderLine> retrievedOrderLines = orderLineService.getOrderLinesByProductId(20L);

		// it works as long as in the Order.json there are ONLY TWO orderline with
		// productId equals to 20
		assertEquals(2, retrievedOrderLines.size());

		assertEquals(20L, retrievedOrderLines.get(0).getProduct().getId());
		assertEquals(14L, retrievedOrderLines.get(0).getOrder().getId());
		assertEquals("Chilli Paste, Sambal Oelek", retrievedOrderLines.get(0).getProduct().getName());
		assertEquals("9400728700", retrievedOrderLines.get(0).getProduct().getSkuCode());
		assertEquals(new BigDecimal("56.12"), retrievedOrderLines.get(0).getProduct().getUnitPrice());
		assertEquals(85, retrievedOrderLines.get(0).getQuantity());
	}

	@Test
	public void testGetOrderLinesByQuantity() {
		List<OrderLine> orderLines = orderLineService.getOrderLinesByQuantity(31);
		assertEquals(2, orderLines.size());
	}

	@Test
	public void testUpdateOrderLineByQuantity() {
		OrderLine updatedOrderLine = orderLineService.updateOrderLineByQuantity(13L, 40);
		assertEquals(40, updatedOrderLine.getQuantity());
	}

	@Test
	public void testUpdateOrderLine() {
		OrderLine retrievedOrderLine = orderLineService.getOrderLineById(8L);

		retrievedOrderLine.setQuantity(44);
		retrievedOrderLine.setProduct(productService.getProductById(13L));
		retrievedOrderLine.setOrder(orderService.getOrderById(17L));

		OrderLine updatedOrderLine = orderLineService.updateOrderLine(retrievedOrderLine);

		assertEquals(44, updatedOrderLine.getQuantity());
		assertEquals(13L, updatedOrderLine.getProduct().getId());
		assertEquals(17L, updatedOrderLine.getOrder().getId());
	}

	@Test
	public void testDeleteOrderLineById() {
		orderLineService.deleteOrderLineById(5L);
		assertNull(orderLineService.getOrderLineById(5L));
	}

	@AfterAll
	public void cleanupChanges() {
		orderLineService.deleteOrderLinesByIdInRange(1L, 50L);
		orderService.deleteOrdersByIdInRange(1L, 50L);
		customerService.deleteCustomersByIdInRange(1L, 50L);
		productService.deleteProductsByIdInRange(1L, 50L);

		testDataLoaderService.resetAutoIncrementForTable("orders", 1L);
		testDataLoaderService.resetAutoIncrementForTable("product", 1L);
		testDataLoaderService.resetAutoIncrementForTable("orderline", 1L);
		testDataLoaderService.resetAutoIncrementForTable("customer", 1L);
	}

}
