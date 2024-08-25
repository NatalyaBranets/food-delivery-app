package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.AuthenticationResponse;
import com.foodhub.delivery_api.dto.RegisterUserRequestDTO;
import com.foodhub.delivery_api.dto.AuthenticationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationController {
    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authRequest);
    ResponseEntity<AuthenticationResponse> register(RegisterUserRequestDTO request);
}
