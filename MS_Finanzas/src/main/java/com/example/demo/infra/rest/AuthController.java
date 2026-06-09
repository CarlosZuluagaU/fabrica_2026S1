package com.example.demo.infra.rest;

import com.example.demo.infra.persistence.entity.TitularEntity;
import com.example.demo.infra.persistence.entity.UsuarioEntity;
import com.example.demo.infra.persistence.repository.JpaTitularRepository;
import com.example.demo.infra.persistence.repository.JpaUsuarioRepository;
import com.example.demo.infra.rest.dto.AuthResponse;
import com.example.demo.infra.rest.dto.LoginRequest;
import com.example.demo.infra.rest.dto.RegisterRequest;
import com.example.demo.infra.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JpaUsuarioRepository usuarioRepository;
    private final JpaTitularRepository titularRepository;

    public AuthController(JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          JpaUsuarioRepository usuarioRepository,
                          JpaTitularRepository titularRepository) {
        this.jwtUtil            = jwtUtil;
        this.passwordEncoder    = passwordEncoder;
        this.usuarioRepository  = usuarioRepository;
        this.titularRepository  = titularRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una cuenta con ese correo"));
        }

        TitularEntity titular = new TitularEntity();
        titular.setNombre(request.name());
        titular.setToken("");
        titular.setZonaHoraria("America/Bogota");
        titular.setMonedaPreferida("COP");
        titular.setFechaRegistro(Instant.now());
        titular = titularRepository.save(titular);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setTitularId(titular.getTitularId());
        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, request.email(), titular.getTitularId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return usuarioRepository.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .map(u -> {
                    String token = jwtUtil.generateToken(request.email());
                    return ResponseEntity.ok(new AuthResponse(token, request.email(), u.getTitularId()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, null)));
    }
}
