package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
