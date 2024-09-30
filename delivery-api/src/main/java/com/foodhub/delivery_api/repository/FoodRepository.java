package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Page<Food> findByRestaurantId(Long restaurantId, Pageable pageable);
}
