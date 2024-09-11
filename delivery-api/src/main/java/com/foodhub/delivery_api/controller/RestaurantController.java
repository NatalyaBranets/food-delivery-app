package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.restaurant.CreateRestaurantRequestDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDataDTO;
import com.foodhub.delivery_api.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody @Valid CreateRestaurantRequestDTO request) {
        RestaurantDTO createdRestaurant = this.restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
    }

    @GetMapping
    public ResponseEntity<RestaurantDataDTO> getAllRestaurants(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                               @RequestParam(name = "query", defaultValue = "") String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(this.restaurantService.getAllRestaurants(page));
        }
        return ResponseEntity.ok(this.restaurantService.searchRestaurants(query, page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable("id") Long id) {
        RestaurantDTO restaurant = this.restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable("id") Long id,
                                              @RequestBody @Valid CreateRestaurantRequestDTO request) {
        RestaurantDTO updatedRestaurant = this.restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRestaurant(@PathVariable("id") Long id) {
        this.restaurantService.deleteRestaurant(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
