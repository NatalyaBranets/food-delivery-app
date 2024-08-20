package com.foodhub.delivery_api.controller.impl;

import com.foodhub.delivery_api.controller.UserController;
import com.foodhub.delivery_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserControllerImpl implements UserController {
    @Autowired
    private UserService userService;
}
