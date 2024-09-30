package com.foodhub.delivery_api.dto.order_item;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequestDTO(
        @NotNull
        Long id,

        @NotEmpty(message = "Quantity should not be empty")
        @Positive
        Integer quantity,

        @NotNull
        BigDecimal price,

        @NotNull
        Long foodId
) {
}
