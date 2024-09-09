package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.UpdateUserRequestDTO;
import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.dto.UsersDataDTO;
import com.foodhub.delivery_api.exception.custom_exceptions.AlreadyExistsException;
import com.foodhub.delivery_api.exception.custom_exceptions.PasswordMatchException;
import com.foodhub.delivery_api.model.Role;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.RoleRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import com.foodhub.delivery_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;
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
        Page<UserDTO> userDTOsPage = this.userRepository.searchUsers(query, pageRequest);
        return new UsersDataDTO(userDTOsPage);
    }

    @Override
    public UserDTO getUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            return new UserDTO(userToUpdate);
        } else {
            throw new NoSuchElementException(String.format("User with id - %s not found", id));
        }
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

            User save = this.userRepository.save(userToUpdate);
            return new UserDTO(save);
        } else {
            throw new NoSuchElementException(String.format("User %s does not exists", request.firstName()));
        }
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NoSuchElementException(String.format("User with id - %s not found", id));
        }
        this.userRepository.deleteById(id);
    }
}
