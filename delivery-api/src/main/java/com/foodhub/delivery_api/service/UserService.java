package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.UpdateUserRequestDTO;
import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.dto.UsersDataDTO;

public interface UserService {
    UsersDataDTO getAllUsers(Integer page);
    UsersDataDTO searchUsers(String query, Integer page);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UpdateUserRequestDTO request);
    void deleteUser(Long id);
}
