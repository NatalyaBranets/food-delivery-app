package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.FoodController;
import com.foodhub.delivery_api.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/foods")
public class FoodControllerImpl implements FoodController {
    @Autowired
    private FoodService foodService;
}
