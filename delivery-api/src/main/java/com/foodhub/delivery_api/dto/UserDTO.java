package com.foodhub.delivery_api.dto;

public record UserDTO (
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address
) {}
