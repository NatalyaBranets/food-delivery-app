package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers(Integer page);
}
