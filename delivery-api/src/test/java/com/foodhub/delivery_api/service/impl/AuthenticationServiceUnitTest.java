package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.config.JwtService;
import com.foodhub.delivery_api.dto.AuthenticationRequest;
import com.foodhub.delivery_api.dto.AuthenticationResponse;
import com.foodhub.delivery_api.dto.RegisterUserRequestDTO;
import com.foodhub.delivery_api.enums.RoleType;
import com.foodhub.delivery_api.exception.AlreadyExistsException;
import com.foodhub.delivery_api.exception.PasswordMatchException;
import com.foodhub.delivery_api.model.Role;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.RoleRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTest {
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @Test
    public void testAuthenticateSuccess() throws Exception {
        // prepare test data
        String username = "test@gmail.com";
        String password = "test";
        AuthenticationRequest request = new AuthenticationRequest(username, password);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        when(this.authenticationManager.authenticate(authentication)).thenReturn(authentication);

        User user = new User();
        when(this.userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        String jwtToken = "jwtToken";
        when(this.jwtService.generateToken(user)).thenReturn(jwtToken);

        AuthenticationResponse expected = new AuthenticationResponse(jwtToken);

        // act
        AuthenticationResponse actual = this.authenticationService.authenticate(request);

        // assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // prepare test data
        String firstName = "test";
        String lastName = "test";
        String username = "test@gmail.com";
        String password = "test";
        String confirmPassword = "test";
        String phone = "+3806744444444";
        String address = "test";

        RegisterUserRequestDTO request = new RegisterUserRequestDTO( firstName, lastName, username, password, confirmPassword, phone, address);

        when(this.userRepository.findByEmail(username)).thenReturn(Optional.empty());

        String encodedPassword = "$2a$10$62RLtXONXv6X4HYAva0Op.MovY44";
        when(this.passwordEncoder.encode(request.password())).thenReturn(encodedPassword);

        Role roleUser = new Role();
        roleUser.setName(RoleType.USER);
        roleUser.setId(1L);
        when(this.roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(roleUser));

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(username);
        user.setPassword(encodedPassword);
        user.setAddress(address);
        user.setPhone(phone);
        when(this.userRepository.save(any(User.class))).thenReturn(user);

        String jwtToken = "jwtToken";
        when(this.jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        AuthenticationResponse expected = new AuthenticationResponse(jwtToken);

        // act
        AuthenticationResponse actual = this.authenticationService.register(request);

        // assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRegisterAlreadyExistsException() throws Exception {
        // prepare test data
        String firstName = "test";
        String lastName = "test";
        String username = "test@gmail.com";
        String password = "test";
        String confirmPassword = "test";
        String phone = "+3806744444444";
        String address = "test";

        RegisterUserRequestDTO request = new RegisterUserRequestDTO( firstName, lastName, username, password, confirmPassword, phone, address);

        String encodedPassword = "$2a$10$62RLtXONXv6X4HYAva0Op.MovY44";
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(username);
        user.setPassword(encodedPassword);
        user.setAddress(address);
        user.setPhone(phone);

        when(this.userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        String expectedMessage = String.format("Email %s already exists", username);

        // act and assert
        Exception exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
            this.authenticationService.register(request);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testRegisterPasswordMatchException() throws Exception {
        // prepare test data
        String firstName = "test";
        String lastName = "test";
        String username = "test@gmail.com";
        String password = "test";
        String confirmPassword = "test1234";
        String phone = "+3806744444444";
        String address = "test";

        RegisterUserRequestDTO request = new RegisterUserRequestDTO(firstName, lastName, username, password, confirmPassword, phone, address);

        String expectedMessage = "Password and confirm password does not match";

        // act and assert
        Exception exception = Assertions.assertThrows(PasswordMatchException.class, () -> {
            this.authenticationService.register(request);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}