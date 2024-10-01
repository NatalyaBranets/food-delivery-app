package com.foodhub.delivery_api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.user.RegisterUserRequestDTO;
import com.foodhub.delivery_api.dto.user.UpdateUserRequestDTO;
import com.foodhub.delivery_api.enums.UserRole;
import com.foodhub.delivery_api.exception.FieldViolation;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.exception.custom_exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@SpringBootTest(classes = TestBeansConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts={"classpath:test-data/create_users.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class UserControllerIntegrationTest {

    private static final String EMAIL_VALID = "admin@gmail.com";

    private static ObjectMapper mapper;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = webAppContextSetup(this.applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // prepare test data
        String firstName = "Anna";
        String lastName = "Lee";
        String email = "not_existssssssss@gmail.com";
        String password =  "anna";
        String confirmPassword =  "anna";
        String phone = "0666666666";
        String address ="Kyiv";
        RegisterUserRequestDTO request = new RegisterUserRequestDTO( firstName, lastName, email, password, confirmPassword, phone, address);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/users/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.verificationCode").exists())
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.isEnabled").value(false));
    }

    @Test
    public void testRegisterDuplicatedEmail() throws Exception {
        // prepare test data
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("Anna", "Lee", EMAIL_VALID, "anna", "anna", "0666666666", "Lviv");

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/users/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(AlreadyExistsException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(String.format("Email %s already exists", EMAIL_VALID)));
    }

    @Test
    public void testRegisterMethodArgumentNotValidException() throws Exception {
        // prepare test data
        RegisterUserRequestDTO request = new RegisterUserRequestDTO( "T", null, "invalid", null, null, null, null);

        // act
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/users/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"))
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseAsMap = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});
        List<FieldViolation> violations = mapper.convertValue(responseAsMap.get("violations"), new TypeReference<List<FieldViolation>>() {});

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
                        .content(mapper.writeValueAsString(request))
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
    public void testDeactivateUser() throws Exception {
        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/users/1/deactivate")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    public void testConfirmEmailFailed() throws Exception {
        // prepare test data
        String code = "HyTGYhGYREd";
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("code", code);

        // act
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/users/verify")
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UsernameNotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(String.format("User with verification code %s not found", code)));
    }
}