package com.foodhub.delivery_api.dto.order;

import com.foodhub.delivery_api.model.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrdersDataDTO (
        List<OrderDTO> data,
        long totalElements,
        int totalPages,
        int currentPage,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * Shorthand constructor with orders and its page.
     * @param ordersDTOs - received from orderPage and mapped from Order
     * @param orderPage - orders page
     */
    public OrdersDataDTO(List<OrderDTO> ordersDTOs, Page<Order> orderPage) {
        this (
                ordersDTOs,
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.getNumber() + 1,
                orderPage.isFirst(),
                orderPage.isLast(),
                orderPage.hasNext(),
                orderPage.hasPrevious()
        );
    }
}
