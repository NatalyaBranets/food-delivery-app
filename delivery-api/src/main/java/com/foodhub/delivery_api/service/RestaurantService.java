package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.restaurant.CreateRestaurantRequestDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDataDTO;
import jakarta.validation.Valid;

public interface RestaurantService {
    RestaurantDTO createRestaurant(@Valid CreateRestaurantRequestDTO request);
    RestaurantDataDTO getAllRestaurants(Integer page);
    RestaurantDataDTO searchRestaurants(String query, Integer page);
    RestaurantDTO getRestaurantById(Long id);
    RestaurantDTO updateRestaurant(Long id, @Valid CreateRestaurantRequestDTO request);
    void deleteRestaurant(Long id);
}
