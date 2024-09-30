package com.foodhub.delivery_api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.rating.CreateRatingRequestDTO;
import com.foodhub.delivery_api.dto.rating.UpdateRatingRequestDTO;
import com.foodhub.delivery_api.repository.RatingRepository;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TestBeansConfiguration.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithMockUser
public class RatingControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RatingRepository ratingRepository;
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
        this.ratingRepository.deleteAll();
        this.restaurantRepository.deleteAll();
    }

    @Test
    @Sql("/test-data/create_restaurants.sql")
    public void testCreateRatingSuccess() throws Exception {
        // prepare test data
        String description = "good";
        int rate = 10;
        long userId = 1;
        long restaurantId = 2;
        CreateRatingRequestDTO request = new CreateRatingRequestDTO(description, rate, userId, restaurantId);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/ratings")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(rate))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_ratings.sql"})
    public void testGetRatingById() throws Exception {
        // prepare test data
        long id = 1;

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/ratings/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("good"))
                .andExpect(jsonPath("$.rating").value(8));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_ratings.sql"})
    public void testUpdateRating() throws Exception {
        // prepare test data
        long id = 1;
        UpdateRatingRequestDTO request = new UpdateRatingRequestDTO("updated", 10);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                    .put("/v1/ratings/" + id)
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("updated"))
                .andExpect(jsonPath("$.rating").value(10));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_ratings.sql"})
    public void testDeleteRating() throws Exception {
        // prepare test data
        long id = 1;

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/ratings/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
