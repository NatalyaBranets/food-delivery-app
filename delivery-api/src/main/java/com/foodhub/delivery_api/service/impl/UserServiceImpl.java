package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.repository.UserRepository;
import com.foodhub.delivery_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
}
