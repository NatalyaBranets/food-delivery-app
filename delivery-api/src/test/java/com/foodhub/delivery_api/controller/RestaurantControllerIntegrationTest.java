package com.foodhub.delivery_api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.restaurant.CreateRestaurantRequestDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.exception.FieldViolation;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TestBeansConfiguration.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithMockUser
public class RestaurantControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestaurantRepository restaurantRepository;

    private static ObjectMapper mapper;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    public void testCreateRestaurantSuccess() throws Exception {
        // prepare test data
        Long id = 1L;
        String name = "Silpo";
        String phone = "123456789";
        String address = "Lviv";

        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setPhone(phone);
        restaurant.setName(name);
        restaurant.setAddress(address);

        when(this.restaurantRepository.existsByNameAndAddress(name, address)).thenReturn(false);
        when(this.restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(name, address, phone);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/restaurants")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.phone").value(phone));

        verify(this.restaurantRepository, times(1)).existsByNameAndAddress(name, address);
        verify(this.restaurantRepository, times(1)).save(any(Restaurant.class));
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testCreateRestaurantDuplicatedAddressAndName() throws Exception {
        // prepare test data
        Long id = 1L;
        String name = "Silpo";
        String phone = "123456789";
        String address = "Lviv";
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(name, address, phone);

        when(this.restaurantRepository.existsByNameAndAddress(name, address)).thenReturn(true);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/restaurants")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(AlreadyExistsException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(String.format("Restaurant %s with address %s already exists", request.name(), request.address())));

        verify(this.restaurantRepository, times(1)).existsByNameAndAddress(name, address);
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testCreateMethodArgumentNotValidException() throws Exception {
        // prepare test data
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO("T", null, null);

        // act
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/restaurants")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"))
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseAsMap = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});
        List<FieldViolation> violations = mapper.convertValue(responseAsMap.get("violations"), new TypeReference<List<FieldViolation>>() {});

        List<FieldViolation> sortedViolationsByField = violations.stream()
                .sorted(Comparator.comparing(FieldViolation::getField))
                .toList();

        FieldViolation addressViolField = sortedViolationsByField.get(0);
        FieldViolation nameViolField = sortedViolationsByField.get(1);
        FieldViolation phoneViolField = sortedViolationsByField.get(2);

        // assert
        assertThat(violations).hasSize(3);

        assertThat(addressViolField.getField()).isEqualTo("address");
        assertThat(addressViolField.getMessage()).isEqualTo("Address should not be empty");

        assertThat(phoneViolField.getField()).isEqualTo("phone");
        assertThat(phoneViolField.getMessage()).isEqualTo("Phone should not be empty");

        assertThat(nameViolField.getField()).isEqualTo("name");
        assertThat(nameViolField.getMessage()).isEqualTo("Invalid Name: Must be of 3 - 30 characters");

        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testGetRestaurantsSuccess() throws Exception {
        // prepare test data
        Integer page = 1;

        Page<RestaurantDTO> restaurantPage = new PageImpl<>(List.of(new RestaurantDTO(1L, "name", "address", "phone")));
        when(this.restaurantRepository.findRestaurants(any(PageRequest.class))).thenReturn(restaurantPage);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", String.valueOf(page));

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/restaurants")
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(this.restaurantRepository, times(1)).findRestaurants(any(PageRequest.class));
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testGetRestaurantsWithQuerySuccess() throws Exception {
        Integer page = 1;
        String query = "name";

        Page<RestaurantDTO> restaurantPage = new PageImpl<>(List.of(new RestaurantDTO(1L, "name", "address", "phone")));
        when(this.restaurantRepository.findRestaurantsByQuery(eq(query), any(PageRequest.class))).thenReturn(restaurantPage);

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", String.valueOf(page));
        requestParams.add("query", query);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/restaurants")
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(this.restaurantRepository, times(1)).findRestaurantsByQuery(eq(query), any(PageRequest.class));
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testGetRestaurantByIdSuccess() throws Exception {
        // prepare test data
        Long id = 1L;
        Restaurant restaurant = new Restaurant();
        when(this.restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/restaurants/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(this.restaurantRepository, times(1)).findById(id);
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testUpdateRestaurant() throws Exception {
        // prepare test data
        Long id = 1L;
        String name = "Silpo";
        String phone = "123456789";
        String address = "Lviv";
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(name, address, phone);

        Restaurant restaurant = new Restaurant(id, name, "Kyiv", phone);
        when(this.restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(this.restaurantRepository.existsByNameAndAddress(name, address)).thenReturn(false);
        Restaurant updatedRestaurant = new Restaurant(id, name, address, phone);
        when(this.restaurantRepository.save(restaurant)).thenReturn(updatedRestaurant);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/restaurants/" + id)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.phone").value(phone));

        verify(this.restaurantRepository, times(1)).findById(id);
        verify(this.restaurantRepository, times(1)).existsByNameAndAddress(name, address);
        verify(this.restaurantRepository, times(1)).save(restaurant);
        verifyNoMoreInteractions(this.restaurantRepository);
    }
}
