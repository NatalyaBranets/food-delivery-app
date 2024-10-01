package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.auth.AuthenticationResponse;
import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = this.authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
