package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
