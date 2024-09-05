package com.foodhub.delivery_api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record RegisterUserRequestDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        String confirmPassword,
        String phone,
        String address) {
}
