package com.foodhub.delivery_api.model;

import com.foodhub.delivery_api.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_date")
    @NotNull
    private LocalDateTime paymentDate;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
