package com.healthtourism.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String city;
    private String address;
    private String dateOfBirth;
    private String gender;
    private String profilePicture;
    private String role;
    private Boolean isActive;
}

