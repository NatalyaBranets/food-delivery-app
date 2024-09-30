package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.food.CreateFoodRequestDTO;
import com.foodhub.delivery_api.dto.food.FoodDTO;
import com.foodhub.delivery_api.dto.food.FoodsDataDTO;
import com.foodhub.delivery_api.dto.food.UpdateFoodRequestDTO;
import com.foodhub.delivery_api.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodDTO> createFood(@RequestBody @Valid CreateFoodRequestDTO request) {
        FoodDTO createdFood = this.foodService.createFood(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<FoodsDataDTO> getAllFoodsByRestaurantId(@PathVariable("restaurantId") Long restaurantId,
                                                                  @RequestParam(name = "page", defaultValue = "1") Integer page) {
        FoodsDataDTO foods = this.foodService.getAllFoodsByRestaurantId(restaurantId, page);
        return ResponseEntity.ok(foods);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodDTO> updateFood(@PathVariable("id") Long foodId,
                                              @RequestBody @Valid UpdateFoodRequestDTO request) {
        FoodDTO updatedFood = this.foodService.updateFood(foodId, request);
        return ResponseEntity.ok(updatedFood);
    }
}
