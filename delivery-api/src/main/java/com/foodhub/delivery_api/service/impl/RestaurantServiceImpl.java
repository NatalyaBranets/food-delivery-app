package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.repository.RestaurantRepository;
import com.foodhub.delivery_api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
}
