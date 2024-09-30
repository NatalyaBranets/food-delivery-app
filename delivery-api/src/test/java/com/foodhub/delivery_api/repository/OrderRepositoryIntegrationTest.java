package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestBeansConfiguration.class})
@Sql(scripts={"classpath:test-data/create_orders.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class OrderRepositoryIntegrationTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testFindByUserId() {
        // prepare test data
        Long userId = 1L;
        Pageable pageRequest = PageRequest.of(1, 10, Sort.Direction.DESC, "orderTime");

        // act
        Page<Order> actual = this.orderRepository.findByUserId(userId, pageRequest);

        // assert
        assertEquals(1, actual.getTotalPages());
        assertEquals(2, actual.getTotalElements());
    }
}
