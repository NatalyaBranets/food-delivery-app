package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.food.CreateFoodRequestDTO;
import com.foodhub.delivery_api.dto.food.FoodDTO;
import com.foodhub.delivery_api.dto.food.FoodsDataDTO;
import com.foodhub.delivery_api.dto.food.UpdateFoodRequestDTO;
import com.foodhub.delivery_api.exception.custom_exceptions.ResourceNotFoundException;
import com.foodhub.delivery_api.model.Food;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import com.foodhub.delivery_api.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public FoodDTO createFood(CreateFoodRequestDTO request) {
        Restaurant restaurant = this.restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Restaurant with id = %s does not exist", request.restaurantId())));

        Food food = new Food();
        food.setName(request.name());
        food.setDescription(request.description());
        food.setPrice(request.price());
        food.setImage(request.image());
        food.setCategory(request.category());
        food.setRestaurant(restaurant);

        // save
        Food savedFood = this.foodRepository.save(food);

        return new FoodDTO(savedFood);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodsDataDTO getAllFoodsByRestaurantId(Long restaurantId, Integer page) {
        boolean isExistsRestaurantById = this.restaurantRepository.existsRestaurantById(restaurantId);
        if (!isExistsRestaurantById) {
            throw new ResourceNotFoundException(String.format("Restaurant with id = %s does not exist", restaurantId));
        }

        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10, Sort.Direction.ASC, "name");
        Page<Food> foodsPage = this.foodRepository.findByRestaurantId(restaurantId, pageRequest);
        List<FoodDTO> foodDTOs = foodsPage.getContent()
                .stream()
                .map(FoodDTO::new)
                .toList();

        return new FoodsDataDTO(foodDTOs, foodsPage);
    }

    @Override
    public FoodDTO updateFood(Long foodId, UpdateFoodRequestDTO request) {
        Food foodToUpdate = this.foodRepository.getReferenceById(foodId);

        foodToUpdate.setName(request.name());
        foodToUpdate.setDescription(request.description());
        foodToUpdate.setPrice(request.price());
        foodToUpdate.setImage(request.image());
        foodToUpdate.setCategory(request.category());

        // update
        Food savedFood = this.foodRepository.save(foodToUpdate);

        return new FoodDTO(savedFood);
    }
}
