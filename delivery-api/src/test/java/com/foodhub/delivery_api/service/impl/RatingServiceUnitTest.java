package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.rating.CreateRatingRequestDTO;
import com.foodhub.delivery_api.dto.rating.RatingDTO;
import com.foodhub.delivery_api.dto.rating.RatingsDataDTO;
import com.foodhub.delivery_api.model.Rating;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.RatingRepository;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class RatingServiceUnitTest {
    @InjectMocks
    private RatingServiceImpl ratingService;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void testCreateRating() {
        // prepare test data
        String description = "good";
        int rate = 10;
        long userId = 1;
        long restaurantId = 2;
        CreateRatingRequestDTO request = new CreateRatingRequestDTO(description, rate, userId, restaurantId);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        when(this.restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        User user = new User();
        user.setId(userId);
        when(this.userRepository.findById(userId)).thenReturn(Optional.of(user));

        Rating rating = new Rating();
        rating.setId(1L);
        rating.setRatingDate(LocalDateTime.now());
        rating.setUser(user);
        rating.setDescription("good");
        rating.setRestaurant(restaurant);
        when(this.ratingRepository.save(any(Rating.class))).thenReturn(rating);

        RatingDTO expected = new RatingDTO(rating);

        // act
        RatingDTO actual = this.ratingService.createRating(request);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        verify(this.restaurantRepository, times(1)).findById(restaurantId);
        verify(this.userRepository, times(1)).findById(userId);
        verify(this.ratingRepository, times(1)).save(any(Rating.class));
        verifyNoMoreInteractions(this.ratingRepository, this.restaurantRepository, this.userRepository);
    }

    @Test
    public void testGetAllRatingsByRestaurantId() {
        // prepare test data
        long restaurantId = 1L;
        int page = 1;

        when(this.restaurantRepository.existsRestaurantById(restaurantId)).thenReturn(true);

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.DESC, "ratingDate");
        List<RatingDTO> ratingDTOList = new ArrayList<>();
        List<Rating> list = new ArrayList<>();
        Page<Rating> ratingDTOsPage = new PageImpl<>(list, pageRequest, list.size());
        when(this.ratingRepository.findByRestaurantId(eq(restaurantId), any(PageRequest.class))).thenReturn(ratingDTOsPage);

        RatingsDataDTO expected = new RatingsDataDTO(ratingDTOList, ratingDTOsPage);

        // act
        RatingsDataDTO actual = this.ratingService.getAllRatingsByRestaurantId(restaurantId, page);

        // assert
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.currentPage(), actual.currentPage());
        Assertions.assertEquals(expected.hasNext(), actual.hasNext());
        Assertions.assertEquals(expected.hasPrevious(), actual.hasPrevious());
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.isFirst(), actual.isFirst());
        Assertions.assertEquals(expected.isLast(), actual.isLast());
        Assertions.assertEquals(expected.data().size(), actual.data().size());
        verify(this.restaurantRepository, times(1)).existsRestaurantById(restaurantId);
        verify(this.ratingRepository, times(1)).findByRestaurantId(eq(restaurantId), any(PageRequest.class));
        verifyNoMoreInteractions(this.restaurantRepository, this.ratingRepository);
    }

    @Test
    public void testGetRatingById() {
        // prepare test data
        long id = 2;

        User user = new User();
        Restaurant restaurant = new Restaurant();
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setRatingDate(LocalDateTime.now());
        rating.setUser(user);
        rating.setDescription("good");
        rating.setRestaurant(restaurant);

        when(this.ratingRepository.findById(id)).thenReturn(Optional.of(rating));

        RatingDTO expected = new RatingDTO(rating);

        // act
        RatingDTO actual = this.ratingService.getRatingById(id);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.ratingRepository, times(1)).findById(id);
        verifyNoMoreInteractions(this.ratingRepository);
    }

    @Test
    public void testDeleteRating() {
        // prepare test data
        Long id = 10L;

        when(this.ratingRepository.existsById(id)).thenReturn(true);

        doNothing().when(this.ratingRepository).deleteById(id);

        // act
        this.ratingService.deleteRating(id);

        // assert
        verify(this.ratingRepository, times(1)).existsById(id);
        verify(this.ratingRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(this.ratingRepository);
    }
}
