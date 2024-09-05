package com.foodhub.delivery_api.dto;

import com.foodhub.delivery_api.model.User;

public record UserDTO (
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address
) {
    public UserDTO(User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );
    }
}
