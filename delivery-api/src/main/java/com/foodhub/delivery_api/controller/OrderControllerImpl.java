package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.model.Order;
import com.foodhub.delivery_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderControllerImpl implements OrderController {

    @Autowired
    private OrderService orderService;

    @Override
    public List<Order> getOrders() {
        return this.orderService.getOrders();
    }
}
