package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.dto.auth.AuthenticationResponse;
import com.foodhub.delivery_api.dto.auth.RegisterUserRequestDTO;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterUserRequestDTO request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
