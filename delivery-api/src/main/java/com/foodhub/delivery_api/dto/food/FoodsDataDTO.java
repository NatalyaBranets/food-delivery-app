package com.foodhub.delivery_api.dto.food;

import com.foodhub.delivery_api.model.Food;
import org.springframework.data.domain.Page;

import java.util.List;

public record FoodsDataDTO(
        List<FoodDTO> data,
        long totalElements,
        int totalPages,
        int currentPage,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * Shorthand constructor with foods and its page.
     * @param foodDTOs - received from foodPage and mapped from Food
     * @param page - foods page
     */
    public FoodsDataDTO(List<FoodDTO> foodDTOs, Page<Food> page) {
        this (
                foodDTOs,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1,
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
