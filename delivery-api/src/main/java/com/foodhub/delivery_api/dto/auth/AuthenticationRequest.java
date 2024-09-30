package com.foodhub.delivery_api.dto.auth;

public record AuthenticationRequest (
        String username,
        String password) {
}
