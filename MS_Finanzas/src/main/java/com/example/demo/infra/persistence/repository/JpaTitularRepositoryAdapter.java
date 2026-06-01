package com.example.demo.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.TitularEntityMapper;

@Component
public class JpaTitularRepositoryAdapter implements TitularRepositoryPort {
    private final JpaTitularRepository jpaTitularRepository;
    private final TitularEntityMapper titularEntityMapper;

    public JpaTitularRepositoryAdapter(JpaTitularRepository jpaTitularRepository, TitularEntityMapper titularEntityMapper) {
        this.jpaTitularRepository = jpaTitularRepository;
        this.titularEntityMapper = titularEntityMapper;
    }

    @Override
    public Optional<Titular> findById(UUID id) {
        return jpaTitularRepository.findById(id)
            .map(titularEntityMapper::toDomain);
    }

    @Override
    public Titular save(Titular titular) {
        return titularEntityMapper.toDomain(
            jpaTitularRepository.save(titularEntityMapper.toEntity(titular)));
    }

    @Override
    public Titular update(UUID id, Titular titular) {
        return titularEntityMapper.toDomain(
            jpaTitularRepository.save(titularEntityMapper.toEntity(titular)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaTitularRepository.deleteById(id);
    }
}
