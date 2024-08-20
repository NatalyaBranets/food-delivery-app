package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodRepository foodRepository;
}
