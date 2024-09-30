package com.foodhub.delivery_api.dto.rating;

import com.foodhub.delivery_api.model.Rating;
import org.springframework.data.domain.Page;

import java.util.List;

public record RatingsDataDTO(
        List<RatingDTO> data,
        long totalElements,
        int totalPages,
        int currentPage,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * Shorthand constructor with ratings and its page.
     * @param ratingDTOs - received from ratingsPage and mapped from Rating
     * @param ratingsPage - ratings page
     */
    public RatingsDataDTO(List<RatingDTO> ratingDTOs, Page<Rating> ratingsPage) {
        this (
                ratingDTOs,
                ratingsPage.getTotalElements(),
                ratingsPage.getTotalPages(),
                ratingsPage.getNumber() + 1,
                ratingsPage.isFirst(),
                ratingsPage.isLast(),
                ratingsPage.hasNext(),
                ratingsPage.hasPrevious()
        );
    }
}
