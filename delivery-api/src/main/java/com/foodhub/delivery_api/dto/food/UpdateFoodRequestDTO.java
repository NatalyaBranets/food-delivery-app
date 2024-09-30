package com.foodhub.delivery_api.dto.food;

import com.foodhub.delivery_api.enums.FoodCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateFoodRequestDTO(
        @NotEmpty(message = "Name should not be empty")
        String name,

        String description,

        byte[] image,

        @NotNull
        BigDecimal price,

        @NotNull
        FoodCategory category
) {
}
