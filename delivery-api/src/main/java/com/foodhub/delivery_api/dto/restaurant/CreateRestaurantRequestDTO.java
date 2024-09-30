package com.foodhub.delivery_api.dto.restaurant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequestDTO(
        @Size(min = 3, max = 30, message = "Invalid Name: Must be of 3 - 30 characters")
        @NotEmpty(message = "Name should not be empty")
        String name,
        @NotEmpty(message = "Address should not be empty")
        String address,
        @NotEmpty(message = "Phone should not be empty")
        String phone
) {
}
