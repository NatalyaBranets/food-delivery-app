package com.foodhub.delivery_api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TestBeansConfiguration.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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

    private static ObjectMapper mapper;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

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
                        .content(mapper.writeValueAsString(request))
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
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist())
                .andExpect(result -> assertInstanceOf(BadCredentialsException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad credentials"));
    }
}