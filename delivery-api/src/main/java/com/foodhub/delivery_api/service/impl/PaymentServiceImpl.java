package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.repository.PaymentRepository;
import com.foodhub.delivery_api.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
}
