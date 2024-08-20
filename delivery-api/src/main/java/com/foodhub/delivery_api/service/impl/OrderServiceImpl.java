package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.model.Order;
import com.foodhub.delivery_api.repository.OrderRepository;
import com.foodhub.delivery_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return this.orderRepository.findAll();
    }


}
