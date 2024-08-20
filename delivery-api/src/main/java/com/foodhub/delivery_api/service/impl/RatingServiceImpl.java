package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.repository.RatingRepository;
import com.foodhub.delivery_api.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;
}
