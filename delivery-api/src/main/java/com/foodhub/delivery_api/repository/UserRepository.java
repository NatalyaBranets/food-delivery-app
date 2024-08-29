package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("select new com.foodhub.delivery_api.dto.UserDTO(u.id, u.firstName, u.lastName, u.email, u.phone, u.address) from User u")
    Page<UserDTO> findUsers(Pageable pageable);
}
