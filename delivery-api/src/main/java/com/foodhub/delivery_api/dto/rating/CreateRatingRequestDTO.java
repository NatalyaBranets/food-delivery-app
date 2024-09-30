package com.foodhub.delivery_api.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRatingRequestDTO(
        @Size(max = 255, message = "Please provide a shorter feedback - less than 255 chars")
        String description,

        @NotNull
        @Min(1)
        @Max(10)
        Integer rating,

        @NotNull
        Long userId,

        @NotNull
        Long restaurantId
) {
}
