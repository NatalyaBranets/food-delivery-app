package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.TestBeansConfiguration;
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
    public void authenticatedUser() throws Exception {
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
}