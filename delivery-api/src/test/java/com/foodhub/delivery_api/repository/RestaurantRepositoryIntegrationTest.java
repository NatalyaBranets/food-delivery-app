package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestBeansConfiguration.class})
@Sql(scripts={"classpath:test-data/create_restaurants.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class RestaurantRepositoryIntegrationTest {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testFindByNameAndAddress() {
        // prepare test data
        String name = "Macdonals";
        String address = "Lviv. Chornovola st";

        // act
        boolean actual = this.restaurantRepository.existsByNameAndAddress(name, address);

        // assert
        assertTrue(actual);
    }

    @Test
    public void testFindRestaurants() {
        // prepare test data
        PageRequest pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC, "name");

        // act
        Page<RestaurantDTO> actual = this.restaurantRepository.findRestaurants(pageRequest);

        // assert
        assertEquals(1, actual.getTotalPages());
    }

    @Test
    public void testFindRestaurantsByQuery() {
        // prepare test data
        String searchRequest = "1234";
        PageRequest pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC, "name");

        // act
        Page<RestaurantDTO> actual = this.restaurantRepository.findRestaurantsByQuery(searchRequest, pageRequest);

        // assert
        assertEquals(0, actual.getTotalPages());
    }

    @Test
    public void testExistsRestaurantById() {
        // prepare test data
        Long restaurantId = 1L;

        // act
        boolean actual = this.restaurantRepository.existsRestaurantById(restaurantId);

        // assert
        assertTrue(actual);
    }

}
