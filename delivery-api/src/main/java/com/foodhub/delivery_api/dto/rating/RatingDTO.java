package com.foodhub.delivery_api.dto.rating;

import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.dto.user.UserDTO;
import com.foodhub.delivery_api.model.Rating;

import java.time.LocalDateTime;

public record RatingDTO(
        Long id,
        String description,
        Integer rating,
        LocalDateTime ratingDate,
        UserDTO user,
        RestaurantDTO restaurant
) {
    public RatingDTO(Rating rating) {
        this (
                rating.getId(),
                rating.getDescription(),
                rating.getRating(),
                rating.getRatingDate(),
                new UserDTO(rating.getUser()),
                new RestaurantDTO(rating.getRestaurant())
        );
    }
}
