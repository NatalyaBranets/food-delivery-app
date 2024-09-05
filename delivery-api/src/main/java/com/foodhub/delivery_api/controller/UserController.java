package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.dto.UsersDataDTO;
import org.springframework.http.ResponseEntity;

public interface UserController {
    ResponseEntity<UserDTO> authenticatedUser();
    ResponseEntity<UsersDataDTO> getAllUsers(Integer page);
}
