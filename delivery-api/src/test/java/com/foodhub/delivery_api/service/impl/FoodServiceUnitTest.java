package com.foodhub.delivery_api.service.impl;


import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.food.CreateFoodRequestDTO;
import com.foodhub.delivery_api.dto.food.FoodDTO;
import com.foodhub.delivery_api.dto.food.FoodsDataDTO;
import com.foodhub.delivery_api.enums.FoodCategory;
import com.foodhub.delivery_api.model.Food;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.repository.RestaurantRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class FoodServiceUnitTest {
    @InjectMocks
    private FoodServiceImpl foodService;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Test
    public void testCreateFood() {
        // prepare test data
        String name = "potato";
        String description = "fry";
        BigDecimal price = new BigDecimal("12.0");
        FoodCategory category = FoodCategory.MAIN_COURSE;
        Long restaurantId = 1L;
        CreateFoodRequestDTO request = new CreateFoodRequestDTO(name, description, null, price, category, restaurantId);

        Restaurant restaurant = new Restaurant();
        when(this.restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Food food = new Food();
        food.setId(1L);
        food.setCategory(category);
        food.setDescription(description);
        food.setImage(null);
        food.setRestaurant(restaurant);
        food.setPrice(price);
        food.setName(name);
        when(this.foodRepository.save(any(Food.class))).thenReturn(food);

        FoodDTO expected = new FoodDTO(1L, name, description, null, price, category);

        // act
        FoodDTO actual = this.foodService.createFood(request);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
        verify(this.restaurantRepository, times(1)).findById(restaurantId);
        verify(this.foodRepository, times(1)).save(any(Food.class));
        verifyNoMoreInteractions(this.restaurantRepository, this.foodRepository);
    }

    @Test
    public void testGetAllFoodsByRestaurantId() {
        // prepare test data
        Long restaurantId = 1L;
        Integer page = 1;

        when(this.restaurantRepository.existsRestaurantById(restaurantId)).thenReturn(true);

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.Direction.ASC, "name");
        List<Food> list = new ArrayList<>();
        List<FoodDTO> listDTOs = new ArrayList<>();
        Page<Food> foodDTOsPage = new PageImpl<>(list, pageRequest, list.size());
        when(this.foodRepository.findByRestaurantId(eq(restaurantId), any(PageRequest.class))).thenReturn(foodDTOsPage);

        FoodsDataDTO expected = new FoodsDataDTO(listDTOs, foodDTOsPage);

        // act
        FoodsDataDTO actual = this.foodService.getAllFoodsByRestaurantId(restaurantId, page);

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
        verify(this.foodRepository, times(1)).findByRestaurantId(eq(restaurantId), any(PageRequest.class));
        verifyNoMoreInteractions(this.restaurantRepository, this.foodRepository);
    }
}
