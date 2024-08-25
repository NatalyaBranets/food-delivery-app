package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.AuthenticationController;
import com.foodhub.delivery_api.dto.AuthenticationResponse;
import com.foodhub.delivery_api.dto.RegisterUserRequestDTO;
import com.foodhub.delivery_api.dto.AuthenticationRequest;
import com.foodhub.delivery_api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = this.authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterUserRequestDTO request) {
        AuthenticationResponse response = this.authenticationService.register(request);
        return ResponseEntity.ok(response);
    }
}
