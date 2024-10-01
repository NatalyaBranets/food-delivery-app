package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.user.RegisterUserRequestDTO;
import com.foodhub.delivery_api.dto.user.UpdateUserRequestDTO;
import com.foodhub.delivery_api.dto.user.UserDTO;
import com.foodhub.delivery_api.dto.user.UsersDataDTO;
import com.foodhub.delivery_api.enums.UserRole;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.exception.custom_exceptions.PasswordMatchException;
import com.foodhub.delivery_api.exception.custom_exceptions.ResourceNotFoundException;
import com.foodhub.delivery_api.model.Role;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.RoleRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

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
        roleUser.setName(UserRole.USER);
        roleUser.setId(1L);
        when(this.roleRepository.findByName(UserRole.USER)).thenReturn(Optional.of(roleUser));

        Mockito.mockStatic(RandomStringUtils.class);
        String code = "GHtttYdgUOip";
        Mockito.when(RandomStringUtils.randomAlphanumeric(anyInt())).thenReturn(code);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(username);
        user.setPassword(encodedPassword);
        user.setAddress(address);
        user.setPhone(phone);
        user.setActive(true);
        user.setVerificationCode(code);
        user.setEnabled(false);
        when(this.userRepository.save(any(User.class))).thenReturn(user);

        UserDTO expected = new UserDTO(user);

        // act
        UserDTO actual = this.userService.register(request);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.userRepository, times(1)).findByEmail(username);
        verify(this.passwordEncoder, times(1)).encode(request.password());
        verify(this.roleRepository, times(1)).findByName(UserRole.USER);
        verify(this.userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(this.userRepository, this.passwordEncoder, this.roleRepository);
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
        user.setActive(true);

        when(this.userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        String expectedMessage = String.format("Email %s already exists", username);

        // act and assert
        Exception exception = Assertions.assertThrows(AlreadyExistsException.class, () -> {
            this.userService.register(request);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(this.userRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(this.userRepository);
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
            this.userService.register(request);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAllUsersSuccess() throws Exception {
        // prepare test data
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page, 10);

        List<UserDTO> users = new ArrayList<>();
        Page<UserDTO> userDTOsPage = new PageImpl<>(users, pageRequest, users.size());
        when(this.userRepository.findUsers(any(PageRequest.class))).thenReturn(userDTOsPage);

        UsersDataDTO expected = new UsersDataDTO(userDTOsPage);

        // act
        UsersDataDTO actual = this.userService.getAllUsers(page);

        // assert
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.currentPage(), actual.currentPage());
        Assertions.assertEquals(expected.hasNext(), actual.hasNext());
        Assertions.assertEquals(expected.hasPrevious(), actual.hasPrevious());
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.isFirst(), actual.isFirst());
        Assertions.assertEquals(expected.isLast(), actual.isLast());
        Assertions.assertEquals(expected.data().size(), actual.data().size());
        verify(this.userRepository, times(1)).findUsers(any(PageRequest.class));
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void testSearchUsersSuccess() throws Exception {
        // prepare test data
        int page = 1;
        PageRequest pageRequest = PageRequest.of(page, 10);
        String searchRequest = "1234";

        List<UserDTO> users = new ArrayList<>();
        Page<UserDTO> userDTOsPage = new PageImpl<>(users, pageRequest, users.size());
        when(this.userRepository.findUsersByQuery(eq(searchRequest), any(PageRequest.class))).thenReturn(userDTOsPage);

        UsersDataDTO expected = new UsersDataDTO(userDTOsPage);

        // act
        UsersDataDTO actual = this.userService.searchUsers(searchRequest, page);

        // assert
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.currentPage(), actual.currentPage());
        Assertions.assertEquals(expected.hasNext(), actual.hasNext());
        Assertions.assertEquals(expected.hasPrevious(), actual.hasPrevious());
        Assertions.assertEquals(expected.totalPages(), actual.totalPages());
        Assertions.assertEquals(expected.isFirst(), actual.isFirst());
        Assertions.assertEquals(expected.isLast(), actual.isLast());
        Assertions.assertEquals(expected.data().size(), actual.data().size());
        verify(this.userRepository, times(1)).findUsersByQuery(eq(searchRequest), any(PageRequest.class));
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void testGetUserByIdSuccess() throws Exception {
        // prepare test data
        Long id = 1L;
        String firstName = "Anna";
        String lastName = "Lee";
        String email = "anna@gmail.com";
        String password =  "45$3tTThytIOtyf";
        String phone = "+380666666666";
        String address ="Kyiv";
        Set<Role> roles = new HashSet<>() {{
            add(new Role(1L, UserRole.USER));
        }};

        User user = new User();
        user.setId(id);
        user.setRoles(roles);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setActive(true);
        user.setEnabled(true);
        user.setVerificationCode(null);

        when(this.userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDTO expected = new UserDTO(id, firstName, lastName, email, phone, address, null, true, true);

        // act
        UserDTO actual = this.userService.getUserById(id);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void testGetUserByIdResourceNotFoundException() throws Exception {
        // prepare test data
        Long id = 10L;

        String expectedMessage = String.format("User with id - %s not found", id);

        // act and assert
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.userService.getUserById(id);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testUpdateUserResourceNotFoundException() throws Exception {
        // prepare test data
        Long id = 1L;

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

        String expectedMessage = String.format("User %s does not exists", request.firstName());

        // act and assert
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.userService.updateUser(id, request);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeactivateUserSuccess() throws Exception {
        // prepare test data
        Long id = 10L;

        User user = new User();
        user.setId(id);
        user.setActive(true);
        when(this.userRepository.findById(id)).thenReturn(Optional.of(user));

        user.setActive(false);
        when(this.userRepository.save(user)).thenReturn(user);

        UserDTO expected = new UserDTO(user);

        // act
        UserDTO actual = this.userService.deactivateUser(id);

        // assert
        Assertions.assertEquals(expected, actual);
        verify(this.userRepository, times(1)).findById(id);
        verify(this.userRepository, times(1)).save(user);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void testDeactivateUserResourceNotFoundException() throws Exception {
        // prepare test data
        Long id = 10L;

        when(this.userRepository.findById(id)).thenReturn(Optional.empty());

        String expectedMessage = String.format("User with id - %s not found", id);

        // act and assert
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.userService.deactivateUser(id);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(this.userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void testVerifyUser() {
        // prepare test data
        String verificationCode = RandomStringUtils.randomAlphanumeric(64);

        User user = new User();
        when(this.userRepository.findByVerificationCode(verificationCode)).thenReturn(Optional.of(user));

        User expected = new User();
        expected.setEnabled(true);
        expected.setVerificationCode(null);
        when(this.userRepository.save(user)).thenReturn(expected);

        // act
        this.userService.verifyUser(verificationCode);

        // assert
        verify(this.userRepository, times(1)).findByVerificationCode(verificationCode);
        verify(this.userRepository, times(1)).save(user);
        verifyNoMoreInteractions(this.userRepository);
    }

}