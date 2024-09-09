package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.config.JwtService;
import com.foodhub.delivery_api.dto.AuthenticationRequest;
import com.foodhub.delivery_api.dto.AuthenticationResponse;
import com.foodhub.delivery_api.dto.RegisterUserRequestDTO;
import com.foodhub.delivery_api.enums.UserRole;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.exception.custom_exceptions.PasswordMatchException;
import com.foodhub.delivery_api.model.Role;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.RoleRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import com.foodhub.delivery_api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Override
    public AuthenticationResponse register(RegisterUserRequestDTO request) {
        // validate registration form
        if (!request.password().equals(request.confirmPassword())) {
            throw new PasswordMatchException("Password and confirm password does not match.");
        }
        Optional<User> userOptional = this.userRepository.findByEmail(request.email());
        if (userOptional.isPresent()) {
            throw new AlreadyExistsException(String.format("Email %s already exists", request.email()));
        }

        // create user
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(this.passwordEncoder.encode(request.password()));
        user.setAddress(request.address());
        user.setPhone(request.phone());

        Optional<Role> optionalRole = this.roleRepository.findByName(UserRole.USER);
        optionalRole.ifPresent(role -> user.setRoles(new HashSet<>() {{
            add(role);
        }}));

        // save user into db
        this.userRepository.save(user);

        // generate token to send as response to user
        String jwtToken = this.jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(), request.password()
                )
        );
        User user = this.userRepository.findByEmail(request.username())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", request.username())));
        // generate token to send as response to user
        String jwtToken = this.jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }
}
