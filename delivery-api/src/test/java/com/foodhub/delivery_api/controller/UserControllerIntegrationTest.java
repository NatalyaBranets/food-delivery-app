package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.UpdateUserRequestDTO;
import com.foodhub.delivery_api.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@SpringBootTest(classes = TestBeansConfiguration.class)
@Sql(scripts={"classpath:test-data/create_users.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class UserControllerIntegrationTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext applicationContext;


    @BeforeEach
    public void setup() {
        this.mockMvc = webAppContextSetup(this.applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void testGetAllUsers() throws Exception {
        // prepare test data
        Integer page = 1;
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", String.valueOf(page));

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/users")
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @WithMockUser
    public void testGetAllUsersWithQuery() throws Exception {
        // prepare test data
        Integer page = 1;
        String searchRequest = "Andriy";
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("page", String.valueOf(page));
        requestParams.add("query", searchRequest);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/users")
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    public void testAuthenticatedUser() throws Exception {
        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/users/profile")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("admin"))
                .andExpect(jsonPath("$.lastName").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"))
                .andExpect(jsonPath("$.phone").value("+38077777777"))
                .andExpect(jsonPath("$.address").value("Lviv"));
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    public void testGetUserById() throws Exception {
        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("admin"))
                .andExpect(jsonPath("$.lastName").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"))
                .andExpect(jsonPath("$.phone").value("+38077777777"))
                .andExpect(jsonPath("$.address").value("Lviv"));
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    public void testUpdateUser() throws Exception {
        // prepare test data
        String firstName = "Anna";
        String lastName = "Lee";
        String email = "anna@gmail.com";
        String password =  "anna";
        String confirmPassword =  "anna";
        String phone = "+380666666666";
        String address ="Kyiv";
        Set<UserRole> roles = new HashSet<>() {{
            add(UserRole.USER);
        }};
        UpdateUserRequestDTO request = new UpdateUserRequestDTO(firstName, lastName, email, password
                , confirmPassword, phone, address, roles);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/users/1")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.address").value(address));
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    public void testDeleteUser() throws Exception {
        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/v1/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}