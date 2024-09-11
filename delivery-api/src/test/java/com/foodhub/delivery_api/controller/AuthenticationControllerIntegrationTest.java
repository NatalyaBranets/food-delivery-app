package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.dto.auth.RegisterUserRequestDTO;
import com.foodhub.delivery_api.exception.FieldViolation;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TestBeansConfiguration.class)
@AutoConfigureMockMvc
@Sql(scripts={"classpath:test-data/create_users.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class AuthenticationControllerIntegrationTest {
    private static final String EMAIL_VALID = "admin@gmail.com";
    private static final String PASSWORD_VALID = "admin";
    private static final String EMAIL_INVALID = "admin1234@gmail.com";
    private static final String PASSWORD_INVALID = "admin1234";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUpEach() {
        this.userRepository.deleteAll();
    }

    @Test
    public void testAuthenticationSuccess() throws Exception {
        // prepare test data
        AuthenticationRequest request = new AuthenticationRequest(EMAIL_VALID, PASSWORD_VALID);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    public void testAuthenticationFailed() throws Exception {
        // prepare test data
        AuthenticationRequest request = new AuthenticationRequest(EMAIL_INVALID, PASSWORD_INVALID);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist())
                .andExpect(result -> assertInstanceOf(BadCredentialsException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // prepare test data
        RegisterUserRequestDTO request = new RegisterUserRequestDTO( "Anna", "Lee", "anna@gmail.com", "anna", "anna", "+380666666666", "Lviv");

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    public void testRegisterDuplicatedEmail() throws Exception {
        // prepare test data
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("Anna", "Lee", EMAIL_VALID, "anna", "anna", "+380666666666", "Lviv");

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist())
                .andExpect(result -> assertInstanceOf(AlreadyExistsException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(String.format("Email %s already exists", EMAIL_VALID)));
    }

    @Test
    public void testRegisterMethodArgumentNotValidException() throws Exception {
        // prepare test data
        RegisterUserRequestDTO request = new RegisterUserRequestDTO( "T", null, "invalid", null, null, null, null);

        // act
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"))
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseAsMap = new ObjectMapper().readValue(responseString, new TypeReference<Map<String, Object>>() {});
        List<FieldViolation> violations = new ObjectMapper().convertValue(responseAsMap.get("violations"), new TypeReference<List<FieldViolation>>() {});

        List<FieldViolation> sortedViolationsByField = violations.stream()
                .sorted(Comparator.comparing(FieldViolation::getField))
                .toList();

        FieldViolation addressViolField = sortedViolationsByField.get(0);
        FieldViolation emailViolField = sortedViolationsByField.get(1);
        FieldViolation firstNameViolField = sortedViolationsByField.get(2);
        FieldViolation lastNameViolField = sortedViolationsByField.get(3);
        FieldViolation passwordViolField = sortedViolationsByField.get(4);
        FieldViolation phoneViolField = sortedViolationsByField.get(5);

        // assert
        assertThat(violations).hasSize(6);

        assertThat(addressViolField.getField()).isEqualTo("address");
        assertThat(addressViolField.getMessage()).isEqualTo("Address should not be empty");

        assertThat(emailViolField.getField()).isEqualTo("email");
        assertThat(emailViolField.getMessage()).isEqualTo("Invalid email");

        assertThat(firstNameViolField.getField()).isEqualTo("firstName");
        assertThat(firstNameViolField.getMessage()).isEqualTo("Invalid First Name: Must be of 3 - 30 characters");

        assertThat(lastNameViolField.getField()).isEqualTo("lastName");
        assertThat(lastNameViolField.getMessage()).isEqualTo("Last name should not be empty");

        assertThat(passwordViolField.getField()).isEqualTo("password");
        assertThat(passwordViolField.getMessage()).isEqualTo("Password should not be empty");

        assertThat(phoneViolField.getField()).isEqualTo("phone");
        assertThat(phoneViolField.getMessage()).isEqualTo("Phone should not be empty");
    }

}