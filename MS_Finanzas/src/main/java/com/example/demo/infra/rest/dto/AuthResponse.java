package com.example.demo.infra.rest.dto;

public record AuthResponse(
    String token,
    String email
) {
}
