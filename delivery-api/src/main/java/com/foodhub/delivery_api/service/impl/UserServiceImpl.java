package com.foodhub.delivery_api.service.impl;

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
import com.foodhub.delivery_api.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDTO register(RegisterUserRequestDTO request) {
        // validate registration form
        if (!request.password().equals(request.confirmPassword())) {
            throw new PasswordMatchException("Password and confirm password does not match.");
        }
        Optional<User> userOptional = this.userRepository.findByEmail(request.email());
        if (userOptional.isPresent()) {
            throw new AlreadyExistsException(String.format("Email %s already exists", request.email()));
        }

        // create user
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(this.passwordEncoder.encode(request.password()));
        user.setAddress(request.address());
        user.setPhone(request.phone());
        user.setActive(true);
        user.setEnabled(false);

        Optional<Role> optionalRole = this.roleRepository.findByName(UserRole.USER);
        optionalRole.ifPresent(role -> user.setRoles(new HashSet<>() {{
            add(role);
        }}));

        String randomCode = RandomStringUtils.randomAlphanumeric(64);
        user.setVerificationCode(randomCode);

        // save user into db
        User save = this.userRepository.save(user);
        return new UserDTO(save);
    }

    @Override
    @Transactional(readOnly = true)
    public UsersDataDTO getAllUsers(Integer page) {
        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10);
        Page<UserDTO> userDTOsPage = this.userRepository.findUsers(pageRequest);
        return new UsersDataDTO(userDTOsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UsersDataDTO searchUsers(String query, Integer page) {
        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10);
        Page<UserDTO> userDTOsPage = this.userRepository.findUsersByQuery(query, pageRequest);
        return new UsersDataDTO(userDTOsPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id - %s not found", id)));
        return new UserDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserRequestDTO request) {
        // validate update form
        if (!request.password().equals(request.confirmPassword())) {
            throw new PasswordMatchException("Password and confirm password does not match.");
        }

        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();

            // validate and update new email
            if (!userToUpdate.getEmail().equals(request.email())) {
                Optional<User> userByEmail = this.userRepository.findByEmail(request.email());
                userByEmail.ifPresent(user -> {
                    throw new AlreadyExistsException(String.format("Email %s already exists.", request.email()));
                });
                userToUpdate.setEmail(request.email());
            }

            // update roles
            Set<Role> rolesToUpdate = new HashSet<>();
            request.roles().forEach(roleRequest -> {
                Optional<Role> optionalRole = this.roleRepository.findByName(roleRequest);
                optionalRole.ifPresent(rolesToUpdate::add);
            });
            userToUpdate.setRoles(rolesToUpdate);

            // update the rest fields
            userToUpdate.setPassword(this.passwordEncoder.encode(request.password()));
            userToUpdate.setPhone(request.phone());
            userToUpdate.setAddress(request.address());
            userToUpdate.setFirstName(request.firstName());
            userToUpdate.setLastName(request.lastName());

            // update
            User save = this.userRepository.save(userToUpdate);
            return new UserDTO(save);
        } else {
            throw new ResourceNotFoundException(String.format("User %s does not exists", request.firstName()));
        }
    }

    @Override
    public UserDTO deactivateUser(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id - %s not found", id)));

        user.setActive(false);

        // update
        User savedUser = this.userRepository.save(user);
        return new UserDTO(savedUser);
    }

    @Override
    public void deleteNotVerifiedUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public void verifyUser(String verificationCode) {
        Optional<User> userOptional = this.userRepository.findByVerificationCode(verificationCode);
        userOptional.ifPresentOrElse(
                user -> {
                    user.setEnabled(true);
                    user.setVerificationCode(null);

                    this.userRepository.save(user);
                },
                () -> {
                    throw new UsernameNotFoundException(String.format("User with verification code %s not found", verificationCode));
                }
        );
    }
}
