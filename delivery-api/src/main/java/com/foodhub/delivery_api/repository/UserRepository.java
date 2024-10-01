package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.dto.user.UserDTO;
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

    @Query("select new com.foodhub.delivery_api.dto.user.UserDTO(u.id, u.firstName, u.lastName, u.email, u.phone, u.address, u.verificationCode, u.isActive, u.isEnabled) from User u")
    Page<UserDTO> findUsers(Pageable pageable);

    @Query("""
            select new com.foodhub.delivery_api.dto.user.UserDTO(u.id, u.firstName, u.lastName, u.email, u.phone, u.address, u.verificationCode, u.isActive, u.isEnabled) from User u
            where lower(u.firstName) like lower(concat('%', :query, '%'))
            or lower(u.lastName) like lower(concat('%', :query, '%'))
            or lower(u.address) like lower(concat('%', :query, '%'))
            or lower(u.email) like lower(concat('%', :query, '%'))
            or lower(u.phone) like lower(concat('%', :query, '%'))
            """)
    Page<UserDTO> findUsersByQuery(String query, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    Optional<User> findByVerificationCode(String code);
}
