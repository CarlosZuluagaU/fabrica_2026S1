package com.example.demo.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "titular_id")
    private UUID titularId;
}
