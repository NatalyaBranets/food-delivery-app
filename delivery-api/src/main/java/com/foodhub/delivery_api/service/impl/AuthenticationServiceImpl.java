package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.config.JwtService;
import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.dto.auth.AuthenticationResponse;
import com.foodhub.delivery_api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(), request.password()
                )
        );

        // generate token to send as response to user
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String jwtToken = this.jwtService.generateToken(principal);

        return new AuthenticationResponse(jwtToken);
    }
}
