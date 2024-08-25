package com.foodhub.delivery_api.dto;

public record RegisterUserRequestDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        String confirmPassword,
        String phone,
        String address) {
}
