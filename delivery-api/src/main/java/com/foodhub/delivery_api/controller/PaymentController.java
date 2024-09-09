package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
}
