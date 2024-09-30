package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.food.CreateFoodRequestDTO;
import com.foodhub.delivery_api.dto.food.FoodDTO;
import com.foodhub.delivery_api.dto.food.FoodsDataDTO;
import com.foodhub.delivery_api.dto.food.UpdateFoodRequestDTO;

public interface FoodService {
    FoodDTO createFood(CreateFoodRequestDTO request);
    FoodsDataDTO getAllFoodsByRestaurantId(Long restaurantId, Integer page);
    FoodDTO updateFood(Long foodId, UpdateFoodRequestDTO request);
}
