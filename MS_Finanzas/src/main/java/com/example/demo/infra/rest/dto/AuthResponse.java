package com.example.demo.infra.rest.dto;

import java.util.UUID;

public record AuthResponse(
    String token,
    String email,
    UUID titularId
) {}
