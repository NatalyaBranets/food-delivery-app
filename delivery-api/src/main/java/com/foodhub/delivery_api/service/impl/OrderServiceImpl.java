package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.repository.OrderRepository;
import com.foodhub.delivery_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

}
