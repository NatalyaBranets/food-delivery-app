package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.UserDTO;
import com.foodhub.delivery_api.dto.UsersDataDTO;
import com.foodhub.delivery_api.repository.UserRepository;
import com.foodhub.delivery_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UsersDataDTO getAllUsers(Integer page) {
        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10);
        Page<UserDTO> userDTOsPage = this.userRepository.findUsers(pageRequest);
        return new UsersDataDTO(userDTOsPage);
    }
}
