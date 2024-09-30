package com.foodhub.delivery_api.dto.order;

import com.foodhub.delivery_api.dto.order_item.OrderItemDTO;
import com.foodhub.delivery_api.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDTO {
    private UUID id;
    private LocalDateTime orderTime;
    private boolean isPaid;
    private OrderStatus status;
    private List<OrderItemDTO> orderItems;
}
