package com.example.hospital.dto;

import com.example.hospital.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String password;

    // TEMP: for testing roles
    private Role role;
}
