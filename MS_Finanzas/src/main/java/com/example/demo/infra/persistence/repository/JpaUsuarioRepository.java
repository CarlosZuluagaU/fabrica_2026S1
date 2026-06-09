package com.example.demo.infra.persistence.repository;

import com.example.demo.infra.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
