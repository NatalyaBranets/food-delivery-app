package com.foodhub.delivery_api.dto.restaurant;

import org.springframework.data.domain.Page;

import java.util.List;

public record RestaurantsDataDTO(
        List<RestaurantDTO> data,
        long totalElements,
        int totalPages,
        int currentPage,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
     public RestaurantsDataDTO(Page<RestaurantDTO> restaurantPage) {
         this (
                 restaurantPage.getContent(),
                 restaurantPage.getTotalElements(),
                 restaurantPage.getTotalPages(),
                 restaurantPage.getNumber() + 1,
                 restaurantPage.isFirst(),
                 restaurantPage.isLast(),
                 restaurantPage.hasNext(),
                 restaurantPage.hasPrevious()
         );
     }
}
