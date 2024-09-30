package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.model.Food;
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
@Sql(scripts={"classpath:test-data/create_restaurants.sql", "classpath:test-data/create_foods.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class FoodRepositoryIntegrationTest {
    @Autowired
    private FoodRepository foodRepository;

    @Test
    public void testFindByRestaurantId() {
        // prepare test data
        Long restaurantId = 1L;
        Pageable pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC, "name");

        // act
        Page<Food> actual = this.foodRepository.findByRestaurantId(restaurantId, pageRequest);

        // assert
        assertEquals(1, actual.getTotalPages());
        assertEquals(2, actual.getTotalElements());
    }
}
