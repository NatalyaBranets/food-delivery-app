package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.OrderController;
import com.foodhub.delivery_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/orders")
public class OrderControllerImpl implements OrderController {
    @Autowired
    private OrderService orderService;
}
