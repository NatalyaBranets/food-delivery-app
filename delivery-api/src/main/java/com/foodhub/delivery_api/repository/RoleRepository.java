package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
