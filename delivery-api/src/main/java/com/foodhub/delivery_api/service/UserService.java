package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.user.RegisterUserRequestDTO;
import com.foodhub.delivery_api.dto.user.UpdateUserRequestDTO;
import com.foodhub.delivery_api.dto.user.UserDTO;
import com.foodhub.delivery_api.dto.user.UsersDataDTO;

public interface UserService {
    UserDTO register(RegisterUserRequestDTO request);
    UsersDataDTO getAllUsers(Integer page);
    UsersDataDTO searchUsers(String query, Integer page);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UpdateUserRequestDTO request);
    UserDTO deactivateUser(Long id);
    void deleteNotVerifiedUser(Long id);
    void verifyUser(String verificationCode);
}
