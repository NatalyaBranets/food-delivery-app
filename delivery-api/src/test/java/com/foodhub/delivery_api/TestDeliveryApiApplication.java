package com.foodhub.delivery_api;

import org.springframework.boot.SpringApplication;

public class TestDeliveryApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(DeliveryApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
