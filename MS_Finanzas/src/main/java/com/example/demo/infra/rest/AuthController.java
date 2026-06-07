package com.example.demo.infra.rest;

import com.example.demo.infra.rest.dto.AuthResponse;
import com.example.demo.infra.rest.dto.LoginRequest;
import com.example.demo.infra.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.ok(new AuthResponse(token, request.email()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody LoginRequest request) {
        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.ok(new AuthResponse(token, request.email()));
    }
}
