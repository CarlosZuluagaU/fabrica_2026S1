package com.example.demo.infra.rest;

import com.example.demo.application.service.BudgetService;
import com.example.demo.domain.model.Budget;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.BudgetRequestMapper;
import com.example.demo.infra.mapper.BudgetResponseMapper;
import com.example.demo.infra.rest.dto.BudgetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetControllerTest {

    private final StubBudgetService budgetService = new StubBudgetService();
    private BudgetController controller;
    private BudgetResponseMapper responseMapper;
    private BudgetRequestMapper requestMapper;

    private UUID budgetId;
    private Budget budget;
    private BudgetResponse response;

    @BeforeEach
    void setUp() {
        budgetId = UUID.randomUUID();
        UUID titularId = UUID.randomUUID();
        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");
        budget = new Budget(budgetId, BigDecimal.valueOf(1_000_000), Instant.now(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                BigDecimal.valueOf(200_000), BigDecimal.valueOf(800_000), titular);
        response = new BudgetResponse(budgetId, BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(200_000), BigDecimal.valueOf(800_000));
        responseMapper = b -> response;
        requestMapper = r -> budget;
        budgetService.findByIdResult = Optional.of(budget);
        budgetService.findAllResult = List.of(budget);
        budgetService.addResult = budget;
        budgetService.updateResult = budget;
        controller = new BudgetController(budgetService, responseMapper, requestMapper);
    }

    @Test
    void getBudgetById_shouldReturnOkWhenFound() {
        ResponseEntity<BudgetResponse> result = controller.getBudgetById(budgetId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getBudgetById_shouldReturnNotFoundWhenMissing() {
        budgetService.findByIdResult = Optional.empty();
        ResponseEntity<BudgetResponse> result = controller.getBudgetById(UUID.randomUUID());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllBudgets_shouldReturnOkWithList() {
        ResponseEntity<List<BudgetResponse>> result = controller.getAllBudgets();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void createBudget_shouldReturnCreated() {
        ResponseEntity<BudgetResponse> result = controller.createBudget(null);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateBudget_shouldReturnOk() {
        ResponseEntity<BudgetResponse> result = controller.updateBudget(budgetId, null);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteBudget_shouldReturnNoContent() {
        ResponseEntity<Void> result = controller.deleteBudget(budgetId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(budgetService.lastDeletedId).isEqualTo(budgetId);
    }

    private static final class StubBudgetService extends BudgetService {

        Optional<Budget> findByIdResult = Optional.empty();
        List<Budget> findAllResult = List.of();
        Budget addResult;
        Budget updateResult;
        UUID lastDeletedId;

        StubBudgetService() {
            super(null, null, null);
        }

        @Override
        public Optional<Budget> findById(UUID id) { return findByIdResult; }

        @Override
        public List<Budget> findAll() { return findAllResult; }

        @Override
        public Budget addBudget(Budget budget, java.time.LocalDate from, java.time.LocalDate to) {
            return addResult;
        }

        @Override
        public Budget updateBudget(UUID id, Budget budget) { return updateResult; }

        @Override
        public void deleteBudget(UUID id) { lastDeletedId = id; }
    }
}
