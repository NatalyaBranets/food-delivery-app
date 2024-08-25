package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.AuthenticationRequest;
import com.foodhub.delivery_api.dto.AuthenticationResponse;
import com.foodhub.delivery_api.dto.RegisterUserRequestDTO;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterUserRequestDTO request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
