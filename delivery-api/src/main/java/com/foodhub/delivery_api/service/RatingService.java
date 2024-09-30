package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.rating.CreateRatingRequestDTO;
import com.foodhub.delivery_api.dto.rating.RatingDTO;
import com.foodhub.delivery_api.dto.rating.RatingsDataDTO;
import com.foodhub.delivery_api.dto.rating.UpdateRatingRequestDTO;

public interface RatingService {
    RatingsDataDTO getAllRatingsByRestaurantId(Long restaurantId, Integer page);
    RatingDTO createRating(CreateRatingRequestDTO request);
    RatingDTO getRatingById(Long id);
    RatingDTO updateRating(Long id, UpdateRatingRequestDTO request);
    void deleteRating(Long id);
}
