package com.foodhub.delivery_api.dto.auth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record AuthenticationResponse(String token) {
}
