package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.rating.CreateRatingRequestDTO;
import com.foodhub.delivery_api.dto.rating.RatingDTO;
import com.foodhub.delivery_api.dto.rating.RatingsDataDTO;
import com.foodhub.delivery_api.dto.rating.UpdateRatingRequestDTO;
import com.foodhub.delivery_api.exception.custom_exceptions.ResourceNotFoundException;
import com.foodhub.delivery_api.model.Rating;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.RatingRepository;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import com.foodhub.delivery_api.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public RatingsDataDTO getAllRatingsByRestaurantId(Long restaurantId, Integer page) {
        boolean isExistsRestaurantById = this.restaurantRepository.existsRestaurantById(restaurantId);
        if (!isExistsRestaurantById) {
            throw new ResourceAccessException(String.format("Restaurant with id = %s does not exist", restaurantId));
        }
        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10, Sort.Direction.DESC, "ratingDate");
        Page<Rating> ratingsPage = this.ratingRepository.findByRestaurantId(restaurantId, pageRequest);
        List<RatingDTO> ratingDTOs = ratingsPage.getContent()
                .stream()
                .map(RatingDTO::new)
                .toList();

        return new RatingsDataDTO(ratingDTOs, ratingsPage);

    }

    @Override
    public RatingDTO createRating(CreateRatingRequestDTO request) {
        Restaurant restaurant = this.restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Restaurant with id = %s does not exist", request.restaurantId())));
        User user = this.userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id = %s does not exist", request.userId())));

        Rating rating = new Rating();
        rating.setDescription(request.description());
        rating.setRating(request.rating());
        rating.setRatingDate(LocalDateTime.now());
        rating.setUser(user);
        rating.setRestaurant(restaurant);

        Rating savedRating = this.ratingRepository.save(rating);

        return new RatingDTO(savedRating);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingDTO getRatingById(Long id) {
        return this.ratingRepository.findById(id)
                .map(RatingDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Rating with id = %s does not exist", id)));
    }

    @Override
    public RatingDTO updateRating(Long id, UpdateRatingRequestDTO request) {
        Rating ratingToUpdate = this.ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Rating with id = %s does not exist", id)));

        ratingToUpdate.setDescription(request.description());
        ratingToUpdate.setRating(request.rating());
        ratingToUpdate.setRatingDate(LocalDateTime.now());

        Rating savedRating = this.ratingRepository.save(ratingToUpdate);

        return new RatingDTO(savedRating);
    }

    @Override
    public void deleteRating(Long id) {
        boolean isExistsById = this.ratingRepository.existsById(id);
        if (!isExistsById) {
            throw new ResourceNotFoundException(String.format("Rating with id %s not found", id));
        }
        this.ratingRepository.deleteById(id);
    }
}
