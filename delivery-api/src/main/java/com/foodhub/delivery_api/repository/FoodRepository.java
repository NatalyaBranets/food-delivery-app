package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
