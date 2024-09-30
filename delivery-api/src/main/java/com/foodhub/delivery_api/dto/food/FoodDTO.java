package com.foodhub.delivery_api.dto.food;

import com.foodhub.delivery_api.enums.FoodCategory;
import com.foodhub.delivery_api.model.Food;

import java.math.BigDecimal;

public record FoodDTO(
        Long id,
        String name,
        String description,
        byte[] image,
        BigDecimal price,
        FoodCategory category
) {
    public FoodDTO (Food food) {
        this (
                food.getId(),
                food.getName(),
                food.getDescription(),
                food.getImage(),
                food.getPrice(),
                food.getCategory()
        );
    }
}
