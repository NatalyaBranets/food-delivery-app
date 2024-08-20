package com.foodhub.delivery_api.repository;

import com.foodhub.delivery_api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
