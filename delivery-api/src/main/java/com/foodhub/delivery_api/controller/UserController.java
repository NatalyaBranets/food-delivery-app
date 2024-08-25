package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {
    ResponseEntity<User> authenticatedUser();
    ResponseEntity<List<User>> getAllUsers(Integer page);
}
