package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.UsersDataDTO;
import com.foodhub.delivery_api.model.User;
import org.springframework.http.ResponseEntity;

public interface UserController {
    ResponseEntity<User> authenticatedUser();
    ResponseEntity<UsersDataDTO> getAllUsers(Integer page);
}
