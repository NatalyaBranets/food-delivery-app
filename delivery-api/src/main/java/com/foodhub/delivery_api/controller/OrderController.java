package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.model.Order;

import java.util.List;

public interface OrderController {
    List<Order> getOrders();
}
