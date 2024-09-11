package com.foodhub.delivery_api.dto.restaurant;

import org.springframework.data.domain.Page;

import java.util.List;

public record RestaurantDataDTO(
        List<RestaurantDTO> data,
        long totalElements,
        int totalPages,
        int currentPage,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * Shorthand constructor with page.
     * @param restaurantPage Page with restaurants data. Must be set.
     */
     public RestaurantDataDTO(Page<RestaurantDTO> restaurantPage) {
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
