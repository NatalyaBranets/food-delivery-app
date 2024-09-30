package com.foodhub.delivery_api.dto.order;

import com.foodhub.delivery_api.dto.order_item.OrderItemRequestDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequestDTO (
        @NotNull
        List<OrderItemRequestDTO> items,
        @NotNull
        Long userId
) {
}
