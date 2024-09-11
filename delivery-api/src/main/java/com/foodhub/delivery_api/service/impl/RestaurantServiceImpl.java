package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.restaurant.CreateRestaurantRequestDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDTO;
import com.foodhub.delivery_api.dto.restaurant.RestaurantDataDTO;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.model.Restaurant;
import com.foodhub.delivery_api.repository.RestaurantRepository;
import com.foodhub.delivery_api.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public RestaurantDTO createRestaurant(CreateRestaurantRequestDTO request) {
        // validate unique name and address
        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findByNameAndAddress(request.name(), request.address());
        restaurantOptional.ifPresent(restaurant -> {
            throw new AlreadyExistsException(String.format("Restaurant %s with address %s already exists", request.name(), request.address()));
        });

        // create dto
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.name());
        restaurant.setAddress(request.address());
        restaurant.setPhone(request.phone());

        // save
        Restaurant savedRestaurant = this.restaurantRepository.save(restaurant);

        return new RestaurantDTO(savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDataDTO getAllRestaurants(Integer page) {
        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10);
        Page<RestaurantDTO> restaurantPage = this.restaurantRepository.findRestaurants(pageRequest);
        return new RestaurantDataDTO(restaurantPage);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDataDTO searchRestaurants(String query, Integer page) {
        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10);
        Page<RestaurantDTO> restaurantDTOsPage = this.restaurantRepository.findRestaurantsByQuery(query, pageRequest);
        return new RestaurantDataDTO(restaurantDTOsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurantById(Long id) {
        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
        return restaurantOptional
                .map(RestaurantDTO::new)
                .orElseThrow(() -> new NoSuchElementException(String.format("Restaurant with id %s not found", id)));
    }

    @Override
    public RestaurantDTO updateRestaurant(Long id, CreateRestaurantRequestDTO request) {
        return this.restaurantRepository.findById(id)
                .map(restaurantToUpdate -> {
                    // validate unique name and address and update
                    if (!restaurantToUpdate.getName().equals(request.name()) || !restaurantToUpdate.getAddress().equals(request.address())) {
                        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findByNameAndAddress(request.name(), request.address());
                        restaurantOptional.ifPresent(restaurant -> {
                            throw new AlreadyExistsException(String.format("Restaurant %s with address %s already exists", request.name(), request.address()));
                        });
                        restaurantToUpdate.setName(request.name());
                        restaurantToUpdate.setAddress(request.address());
                    }

                    restaurantToUpdate.setPhone(request.phone());

                    // update
                    Restaurant savedRestaurant = this.restaurantRepository.save(restaurantToUpdate);

                    return new RestaurantDTO(savedRestaurant);
                })
                .orElseThrow(() -> new NoSuchElementException(String.format("Restaurant with id %s not found", id)));
    }

    @Override
    public void deleteRestaurant(Long id) {
        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            throw new NoSuchElementException(String.format("Restaurant with id %s not found", id));
        }
        this.restaurantRepository.deleteById(id);
    }
}
