package com.foodhub.delivery_api.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record UsersDataDTO (
    List<UserDTO> data,
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
     * @param userPage Page with users data. Must be set.
     */
    public UsersDataDTO(Page<UserDTO> userPage) {
        this(userPage.getContent(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber() + 1,
                userPage.isFirst(),
                userPage.isLast(),
                userPage.hasNext(),
                userPage.hasPrevious()
        );
    }
}
