package com.healthtourism.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String country;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private String dateOfBirth;

    @Column
    private String gender; // MALE, FEMALE, OTHER

    @Column
    private String profilePicture;

    @Column(nullable = false)
    private String role; // USER, ADMIN

    @Column(nullable = false)
    private Boolean isActive;
}

