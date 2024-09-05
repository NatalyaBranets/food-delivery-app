package com.foodhub.delivery_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestBeansConfiguration.class)
@SpringBootTest
class DeliveryApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
