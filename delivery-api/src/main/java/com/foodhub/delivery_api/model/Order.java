package com.foodhub.delivery_api.model;

import com.foodhub.delivery_api.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_time")
    @NotNull
    private LocalDateTime orderTime;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "order_food",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "food_id", referencedColumnName="id")
    )
    private List<Food> foods;

    @Formula("(SELECT SUM(f.price) FROM food f JOIN order_food of ON f.id = of.food_id WHERE of.order_id = id)")
    private BigDecimal totalAmount;
}
