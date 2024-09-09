package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.UpdateUserRequestDTO;
import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.dto.UsersDataDTO;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserDTO(authenticatedUser));
    }

    @GetMapping
    public ResponseEntity<UsersDataDTO> getAllUsers(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                    @RequestParam(name = "query", defaultValue = "") String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(this.userService.getAllUsers(page));
        }
        return ResponseEntity.ok(this.userService.searchUsers(query, page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        UserDTO user = this.userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id,
                                              @RequestBody @Valid UpdateUserRequestDTO request) {
        UserDTO updatedUser = this.userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
