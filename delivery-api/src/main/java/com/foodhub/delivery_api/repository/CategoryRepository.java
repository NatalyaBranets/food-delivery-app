package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
