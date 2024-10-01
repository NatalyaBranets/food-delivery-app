package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.config.JwtService;
import com.foodhub.delivery_api.dto.auth.AuthenticationRequest;
import com.foodhub.delivery_api.dto.auth.AuthenticationResponse;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTest {
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;
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

        User user = new User();
        user.setActive(true);
        user.setEmail(username);
        user.setPassword(password);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, request);
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String jwtToken = "jwtToken";
        when(this.jwtService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        AuthenticationResponse expected = new AuthenticationResponse(jwtToken);

        // act
        AuthenticationResponse actual = this.authenticationService.authenticate(request);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(this.jwtService, times(1)).generateToken(any(UserDetails.class));
        verifyNoMoreInteractions(this.authenticationManager, this.jwtService, this.userRepository);
    }

    @Test
    public void testAuthenticateFailed() throws Exception {
        // prepare test data
        String username = "test@gmail.com";
        String password = "test";
        AuthenticationRequest request = new AuthenticationRequest(username, password);

        String expected = "Invalid credentials";
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException(expected));

        // act
        Exception exception = Assertions.assertThrows(BadCredentialsException.class, () -> {
            this.authenticationService.authenticate(request);
        });
        String actualMessage = exception.getMessage();

        // assert
        assertTrue(actualMessage.contains(expected));
    }
}