package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.restaurant.CreateRestaurantRequestDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantsDataDTO;
import jakarta.validation.Valid;

public interface RestaurantService {
    RestaurantDTO createRestaurant(@Valid CreateRestaurantRequestDTO request);
    RestaurantsDataDTO getAllRestaurants(Integer page);
    RestaurantsDataDTO searchRestaurants(String query, Integer page);
    RestaurantDTO getRestaurantById(Long id);
    RestaurantDTO updateRestaurant(Long id, @Valid CreateRestaurantRequestDTO request);
}
