package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
