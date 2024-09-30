package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.order.CreateOrderRequestDTO;
import com.foodhub.delivery_api.dto.order.OrderDTO;
import com.foodhub.delivery_api.dto.order.OrdersDataDTO;
import com.foodhub.delivery_api.dto.order.UpdateOrderStatusRequestDTO;

import java.util.UUID;

public interface OrderService {
    OrderDTO placeOrder(CreateOrderRequestDTO request);
    OrdersDataDTO getAllOrdersByUserId(Long userId, Integer page);
    OrderDTO updateOrderStatus(UUID orderId, UpdateOrderStatusRequestDTO request);
    void deleteOrder(UUID id);
    void deleteItemFromOrder(UUID orderId, Long itemId);
}
