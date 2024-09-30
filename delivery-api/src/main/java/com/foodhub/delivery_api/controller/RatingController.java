package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.rating.CreateRatingRequestDTO;
import com.foodhub.delivery_api.dto.rating.RatingDTO;
import com.foodhub.delivery_api.dto.rating.RatingsDataDTO;
import com.foodhub.delivery_api.dto.rating.UpdateRatingRequestDTO;
import com.foodhub.delivery_api.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RatingsDataDTO> getAllRatingsByRestaurantId(@PathVariable("restaurantId") Long restaurantId,
                                                                      @RequestParam(name = "page", defaultValue = "1") Integer page) {
        RatingsDataDTO ratingsData = this.ratingService.getAllRatingsByRestaurantId(restaurantId, page);
        return ResponseEntity.ok(ratingsData);
    }

    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@RequestBody @Valid CreateRatingRequestDTO request) {
        RatingDTO createdRating = this.ratingService.createRating(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable("id") Long id) {
        RatingDTO rating = this.ratingService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updateRating(@PathVariable("id") Long id,
                                                  @RequestBody @Valid UpdateRatingRequestDTO request) {
        RatingDTO updatedRating = this.ratingService.updateRating(id, request);
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRating(@PathVariable("id") Long id) {
        this.ratingService.deleteRating(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
