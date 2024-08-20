package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.RestaurantController;
import com.foodhub.delivery_api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantControllerImpl implements RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
}
