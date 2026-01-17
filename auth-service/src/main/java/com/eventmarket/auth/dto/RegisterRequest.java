package com.eventmarket.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private String companyName;
    private String inn;

    private String role;
}
