package com.example.demo.infra.rest;

import com.example.demo.application.service.TitularService;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.TitularRequestMapper;
import com.example.demo.infra.mapper.TitularResponseMapper;
import com.example.demo.infra.rest.dto.TitularResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TitularControllerTest {

    private final StubTitularService titularService = new StubTitularService();
    private TitularController controller;

    private UUID titularId;
    private Titular titular;
    private TitularResponse response;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");
        response = new TitularResponse(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", titular.fechaRegistro(), "COP", "America/Bogota");
        TitularResponseMapper responseMapper = t -> response;
        TitularRequestMapper requestMapper = r -> titular;
        titularService.findByIdResult = Optional.of(titular);
        titularService.createResult = titular;
        titularService.updateResult = titular;
        controller = new TitularController(titularService, requestMapper, responseMapper);
    }

    @Test
    void getById_shouldReturnOkWhenFound() {
        ResponseEntity<TitularResponse> result = controller.getById(titularId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getById_shouldReturnNotFoundWhenMissing() {
        titularService.findByIdResult = Optional.empty();
        ResponseEntity<TitularResponse> result = controller.getById(UUID.randomUUID());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void create_shouldReturnCreated() {
        ResponseEntity<TitularResponse> result = controller.create(null);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void update_shouldReturnOk() {
        ResponseEntity<TitularResponse> result = controller.update(titularId, null);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void delete_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.delete(titularId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(titularService.lastDeletedId).isEqualTo(titularId);
    }

    private static final class StubTitularService extends TitularService {

        Optional<Titular> findByIdResult = Optional.empty();
        Titular createResult;
        Titular updateResult;
        UUID lastDeletedId;

        StubTitularService() { super(null); }

        @Override
        public Optional<Titular> findById(UUID id) { return findByIdResult; }

        @Override
        public Titular createTitular(Titular t) { return createResult; }

        @Override
        public Titular updateTitular(UUID id, Titular t) { return updateResult; }

        @Override
        public void deleteTitular(UUID id) { lastDeletedId = id; }
    }
}
