package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.dto.auth.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
