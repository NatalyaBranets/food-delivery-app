package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.model.Order;
import com.foodhub.delivery_api.model.Rating;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestBeansConfiguration.class})
@Sql(scripts={"classpath:test-data/create_restaurants.sql", "classpath:test-data/create_ratings.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class RatingRepositoryIntegrationTest {
    @Autowired
    private RatingRepository ratingRepository;

    @Test
    public void testFindByRestaurantId() {
        // prepare test data
        Long restaurantId = 1L;
        Pageable pageRequest = PageRequest.of(1, 10, Sort.Direction.DESC, "ratingDate");

        // act
        Page<Rating> actual = this.ratingRepository.findByRestaurantId(restaurantId, pageRequest);

        // assert
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getTotalElements());
    }

    @Test
    public void testExistsById() {
        // prepare test data
        Long ratingId = 1L;

        // act
        boolean actual = this.ratingRepository.existsById(ratingId);

        // assert
        assertTrue(actual);
    }
}
