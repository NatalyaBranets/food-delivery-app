package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.UsersDataDTO;

public interface UserService {
    UsersDataDTO getAllUsers(Integer page);
    UsersDataDTO searchUsers(String query, Integer page);
}
