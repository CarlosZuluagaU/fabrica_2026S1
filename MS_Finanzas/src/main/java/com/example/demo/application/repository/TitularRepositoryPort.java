package com.example.demo.application.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.domain.model.Titular;

public interface TitularRepositoryPort {
    Optional<Titular> findById(UUID titularId);
    Titular save(Titular titular);
    Titular update(UUID id, Titular titular);
    void deleteById(UUID id);
}
