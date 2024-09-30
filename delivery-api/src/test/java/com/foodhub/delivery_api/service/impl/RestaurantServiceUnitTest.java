package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.restaurant.CreateRestaurantRequestDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantsDataDTO;
import com.foodhub.delivery_api.exception.custom_exceptions.ResourceNotFoundException;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class RestaurantServiceUnitTest {
    @InjectMocks
    private RestaurantServiceImpl restaurantService;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Test
    public void testCreateRestaurant() throws Exception {
        // prepare test data
        Long id = 1L;
        String name = "Maestro";
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

        RestaurantDTO expected = new RestaurantDTO(id, name, address, phone);

        // act
        RestaurantDTO actual = this.restaurantService.createRestaurant(request);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.restaurantRepository, times(1)).existsByNameAndAddress(name, address);
        verify(this.restaurantRepository, times(1)).save(any(Restaurant.class));
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testGetAllRestaurants() throws Exception {
        // prepare test database
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.ASC, "name");

        List<RestaurantDTO> list = new ArrayList<>();
        Page<RestaurantDTO> rastaurantDTOsPage = new PageImpl<>(list, pageRequest, list.size());
        when(this.restaurantRepository.findRestaurants(any(PageRequest.class))).thenReturn(rastaurantDTOsPage);

        RestaurantsDataDTO expected = new RestaurantsDataDTO(rastaurantDTOsPage);

        // act
        RestaurantsDataDTO actual = this.restaurantService.getAllRestaurants(page);

        // assert
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.currentPage(), actual.currentPage());
        Assertions.assertEquals(expected.hasNext(), actual.hasNext());
        Assertions.assertEquals(expected.hasPrevious(), actual.hasPrevious());
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.isFirst(), actual.isFirst());
        Assertions.assertEquals(expected.isLast(), actual.isLast());
        Assertions.assertEquals(expected.data().size(), actual.data().size());
        verify(this.restaurantRepository, times(1)).findRestaurants(any(PageRequest.class));
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testSearchRestaurants() throws Exception {
        // prepare test data
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.ASC, "name");
        String searchRequest = "1234";

        List<RestaurantDTO> restaurants = new ArrayList<>();
        Page<RestaurantDTO> restaurantDTOsPage = new PageImpl<>(restaurants, pageRequest, restaurants.size());
        when(this.restaurantRepository.findRestaurantsByQuery(eq(searchRequest), any(PageRequest.class))).thenReturn(restaurantDTOsPage);

        RestaurantsDataDTO expected = new RestaurantsDataDTO(restaurantDTOsPage);

        // act
        RestaurantsDataDTO actual = this.restaurantService.searchRestaurants(searchRequest, page);

        // assert
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.currentPage(), actual.currentPage());
        Assertions.assertEquals(expected.hasNext(), actual.hasNext());
        Assertions.assertEquals(expected.hasPrevious(), actual.hasPrevious());
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.isFirst(), actual.isFirst());
        Assertions.assertEquals(expected.isLast(), actual.isLast());
        Assertions.assertEquals(expected.data().size(), actual.data().size());
        verify(this.restaurantRepository, times(1)).findRestaurantsByQuery(eq(searchRequest), any(PageRequest.class));
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testGetRestaurantById() throws Exception {
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

        when(this.restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

        RestaurantDTO expected = new RestaurantDTO(id, name, address, phone);

        // act
        RestaurantDTO actual = this.restaurantService.getRestaurantById(id);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.restaurantRepository, times(1)).findById(id);
        verifyNoMoreInteractions(this.restaurantRepository);
    }

    @Test
    public void testUpdateRestaurantResourceNotFoundException() throws Exception {
        // prepare test data
        Long id = 1L;
        String name = "Silpo";
        String phone = "123456789";
        String address = "Lviv";

        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(name, address, phone);

        when(this.restaurantRepository.findById(id)).thenReturn(Optional.empty());

        String expectedMessage = String.format("Restaurant with id %s not found", id);

        // act and assert
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.restaurantService.updateRestaurant(id, request);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(this.restaurantRepository, times(1)).findById(id);
        verifyNoMoreInteractions(this.restaurantRepository);
    }
}
