package com.foodhub.delivery_api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.food.CreateFoodRequestDTO;
import com.foodhub.delivery_api.dto.food.UpdateFoodRequestDTO;
import com.foodhub.delivery_api.enums.FoodCategory;
import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TestBeansConfiguration.class)
@AutoConfigureMockMvc
@WithMockUser
public class FoodControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    private static ObjectMapper mapper;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @AfterEach
    void cleanUpEach() {
        this.foodRepository.deleteAll();
        this.restaurantRepository.deleteAll();
    }

    @Test
    @Sql("/test-data/create_restaurants.sql")
    public void testCreateFoodSuccess() throws Exception {
        // prepare test data
        String name = "potato";
        String description = "fry";
        BigDecimal price = new BigDecimal("12.0");
        FoodCategory category = FoodCategory.MAIN_COURSE;
        Long restaurantId = 1L;
        CreateFoodRequestDTO request = new CreateFoodRequestDTO(name, description, null, price, category, restaurantId);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/foods")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value(category.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(price));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql"})
    public void testGetAllFoodsByRestaurantId() throws Exception {
        // prepare test data
        long restaurantId = 1L;
        int page = 1;
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", String.valueOf(page));

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/foods/restaurant/" + restaurantId)
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql"})
    public void testUpdateFood() throws Exception {
        // prepare test data
        long foodId = 2L;
        String name = "potato";
        String description = "fry";
        BigDecimal price = new BigDecimal("12.0");
        FoodCategory category = FoodCategory.MAIN_COURSE;
        UpdateFoodRequestDTO request = new UpdateFoodRequestDTO(name, description, null, price, category);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/foods/" + foodId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.category").value(category.name()));
    }
}
