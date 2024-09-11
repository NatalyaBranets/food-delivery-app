package com.foodhub.delivery_api.dto.restaurant;

import com.foodhub.delivery_api.model.Restaurant;

public record RestaurantDTO (
        Long id,
        String name,
        String address,
        String phone
) {
    public RestaurantDTO (Restaurant restaurant) {
        this (
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPhone()
        );
    }
}
