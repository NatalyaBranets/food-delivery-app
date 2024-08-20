package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
