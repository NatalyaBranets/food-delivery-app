package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.UserController;
import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.dto.UsersDataDTO;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @Override
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserDTO(authenticatedUser));
    }

    @Override
    @GetMapping
    public ResponseEntity<UsersDataDTO> getAllUsers(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        UsersDataDTO usersData = this.userService.getAllUsers(page);
        return ResponseEntity.ok(usersData);
    }
}
