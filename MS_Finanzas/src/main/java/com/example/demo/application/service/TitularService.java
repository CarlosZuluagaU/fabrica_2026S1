package com.example.demo.application.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.application.repository.TitularRepositoryPort;

import com.example.demo.application.usecase.CreateTitularUseCase;
import com.example.demo.application.usecase.DeleteTitularUseCase;
import com.example.demo.application.usecase.GetTitularUseCase;
import com.example.demo.application.usecase.UpdateTitularUseCase;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Titular;

@Service
public class TitularService implements GetTitularUseCase, CreateTitularUseCase, UpdateTitularUseCase, DeleteTitularUseCase {

    private final TitularRepositoryPort titularRepositoryPort;

    public TitularService(TitularRepositoryPort titularRepositoryPort) {
        this.titularRepositoryPort = titularRepositoryPort;
    }

    @Override
    public Optional<Titular> findById(UUID id) {
        return titularRepositoryPort.findById(id);
    }

    @Override
    public Titular createTitular(Titular titular) {
        Titular prepared = new Titular(
            titular.titularId(),
            titular.nombre(),
            titular.primerApellido(),
            titular.segundoApellido(),
            titular.telefono(),
            titular.fechaRegistro() != null ? titular.fechaRegistro() : Instant.now(),
            titular.monedaPreferida(),
            titular.zonaHoraria(),
            titular.token() != null ? titular.token() : UUID.randomUUID().toString()
        );
        return titularRepositoryPort.save(prepared);
    }

    @Override
    public Titular updateTitular(UUID id, Titular titular) {
        titularRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado con id: " + id));
        return titularRepositoryPort.update(id, titular);
    }

    @Override
    public void deleteTitular(UUID id) {
        titularRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado con id: " + id));
        titularRepositoryPort.deleteById(id);
    }

}
