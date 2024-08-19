package com.foodhub.delivery_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @SequenceGenerator(name = "usr_id_seq_gen", sequenceName = "usr_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_id_seq_gen")
    private Long id;

    @Column(name = "first_name")
    @NotNull
    private String firstName;

    @Column(name = "last_name")
    @NotNull
    private String lastName;

    @Column(name = "email", unique = true)
    @NotNull
    private String email;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "phone")
    @NotNull
    private String phone;

    @Column(name = "address")
    @NotNull
    private String address;
}
