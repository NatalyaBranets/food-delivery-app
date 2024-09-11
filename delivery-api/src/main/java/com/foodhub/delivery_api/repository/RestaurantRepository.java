package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByNameAndAddress(String name, String address);

    @Query("select new com.foodhub.delivery_api.dto.restaurant.RestaurantDTO(r.id, r.name, r.address, r.phone) from Restaurant r")
    Page<RestaurantDTO> findRestaurants(Pageable pageable);

    @Query("""
            select new com.foodhub.delivery_api.dto.restaurant.RestaurantDTO(r.id, r.name, r.address, r.phone) from Restaurant r
            where lower(r.name) like lower(concat('%', :query, '%'))
            or lower(r.address) like lower(concat('%', :query, '%'))
            """)
    Page<RestaurantDTO> findRestaurantsByQuery(String query, Pageable pageable);
}
