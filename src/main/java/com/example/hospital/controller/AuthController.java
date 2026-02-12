package com.example.hospital.controller;

import com.example.hospital.dto.*;
import com.example.hospital.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return new AuthResponse(token);
    }
}
