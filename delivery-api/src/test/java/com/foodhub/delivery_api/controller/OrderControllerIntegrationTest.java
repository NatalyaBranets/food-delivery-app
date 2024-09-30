package com.foodhub.delivery_api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.order.CreateOrderRequestDTO;
import com.foodhub.delivery_api.dto.order.UpdateOrderStatusRequestDTO;
import com.foodhub.delivery_api.dto.order_item.OrderItemRequestDTO;
import com.foodhub.delivery_api.enums.OrderStatus;
import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.repository.OrderItemRepository;
import com.foodhub.delivery_api.repository.OrderRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TestBeansConfiguration.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithMockUser
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
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
        this.orderItemRepository.deleteAll();
        this.orderRepository.deleteAll();
        this.foodRepository.deleteAll();
        this.restaurantRepository.deleteAll();
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql"})
    public void testPlaceOrder() throws Exception {
        // prepare test data
        long userId = 1;
        OrderItemRequestDTO dto1 = new OrderItemRequestDTO(1L, 4, new BigDecimal(5), 1L);
        List<OrderItemRequestDTO> items = Collections.singletonList(dto1);
        CreateOrderRequestDTO request = new CreateOrderRequestDTO(items, userId);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/orders")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderTime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.paid").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(OrderStatus.PROCESSING.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderItems.length()").value(1));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql", "/test-data/create_orders.sql", "/test-data/create_order_items.sql"})
    public void testGetAllOrdersByUserId() throws Exception {
        // prepare test data
        long userId = 1;

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders/user/" + userId)
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
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql", "/test-data/create_orders.sql", "/test-data/create_order_items.sql"})
    public void testUpdateOrderStatus() throws Exception {
        // prepare test data
        UUID id = UUID.fromString("50c3a35d-2c40-4987-a9dd-7aaa987a79b4");
        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO(OrderStatus.DELIVERED);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/orders/" + id + "/status")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderTime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.paid").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(OrderStatus.DELIVERED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderItems.length()").value(1));
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql", "/test-data/create_orders.sql", "/test-data/create_order_items.sql"})
    public void testDeleteOrder() throws Exception {
        // prepare test data
        UUID id = UUID.fromString("50c3a35d-2c40-4987-a9dd-7aaa987a79b4");

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/orders/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql({"/test-data/create_restaurants.sql", "/test-data/create_foods.sql", "/test-data/create_orders.sql", "/test-data/create_order_items.sql"})
    public void testDeleteItemFromOrder() throws Exception {
        // prepare test data
        UUID id = UUID.fromString("50c3a35d-2c40-4987-a9dd-7aaa987a79b4");
        long itemId = 1L;

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/orders/" + id +"/item/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}