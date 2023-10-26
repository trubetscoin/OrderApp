package com.example.OrderApp;

import com.example.OrderApp.service.DataInitializationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OrderAppApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(OrderAppApplication.class, args);
		String[] activeProfiles = context.getEnvironment().getActiveProfiles();
		for (String profile : activeProfiles) {
			if (profile.equals("data-init")) {
				DataInitializationService dataInitializationService = context.getBean(DataInitializationService.class);
				dataInitializationService.generateMockarooCustomers();
				dataInitializationService.generateMockarooProducts();
				dataInitializationService.generateMockarooOrders();
				dataInitializationService.generateMockarooOrderLines();
				break;
			}
		}
	}

}
