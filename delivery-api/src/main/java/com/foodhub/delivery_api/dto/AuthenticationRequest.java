package com.foodhub.delivery_api.dto;

public record AuthenticationRequest(
        String username,
        String password) {
}
