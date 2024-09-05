package com.foodhub.delivery_api;

import org.springframework.boot.SpringApplication;

/**
 * Class created for manual testing.
 * Run it and then follow to browser to test app.
 */
public class TestDeliveryApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(DeliveryApiApplication::main)
				.with(TestBeansConfiguration.class)
				.run(args);
	}
}
