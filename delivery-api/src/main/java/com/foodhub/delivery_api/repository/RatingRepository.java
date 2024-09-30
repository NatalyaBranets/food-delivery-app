package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByRestaurantId(Long restaurantId, Pageable pageable);
    boolean existsById(Long id);
}
