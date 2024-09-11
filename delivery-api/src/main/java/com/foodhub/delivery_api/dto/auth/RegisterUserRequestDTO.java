package com.foodhub.delivery_api.dto.auth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record RegisterUserRequestDTO(
        @Size(min = 3, max = 30, message = "Invalid First Name: Must be of 3 - 30 characters")
        @NotEmpty(message = "First name should not be empty")
        String firstName,

        @Size(min = 3, max = 30, message = "Invalid Last Name: Must be of 3 - 30 characters")
        @NotEmpty(message = "Last name should not be empty")
        String lastName,

        @Email(message = "Invalid email")
        @NotEmpty(message = "Email should not be empty")
        String email,

        @NotEmpty(message = "Password should not be empty")
        String password,

        String confirmPassword,

        @NotEmpty(message = "Phone should not be empty")
        String phone,

        @NotEmpty(message = "Address should not be empty")
        String address) {
}
