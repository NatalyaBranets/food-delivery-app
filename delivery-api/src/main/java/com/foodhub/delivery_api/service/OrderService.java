package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> getOrders();
}
