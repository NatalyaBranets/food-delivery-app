package com.foodhub.delivery_api.dto.order;

import com.foodhub.delivery_api.enums.OrderStatus;

public record UpdateOrderStatusRequestDTO(
        OrderStatus status
) {
}
