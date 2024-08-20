package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.RatingController;
import com.foodhub.delivery_api.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ratings")
public class RatingControllerImpl implements RatingController {
    @Autowired
    private RatingService ratingService;
}
