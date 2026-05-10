package com.example.demo.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.application.repository.SavingGoalRepositoryPort;
import com.example.demo.application.usecase.AddSavingGoalUseCase;
import com.example.demo.application.usecase.RemoveSavingGoalUseCase;
import com.example.demo.application.usecase.RetrieveSavingGoalUseCase;
import com.example.demo.application.usecase.UpdateSavingGoalUseCase;
import com.example.demo.domain.exception.DuplicateGoalNameException;
import com.example.demo.domain.exception.SavingGoalNotFoundException;
import com.example.demo.domain.model.GoalStatus;
import com.example.demo.domain.model.SavingGoal;

@Service
public class SavingGoalService implements AddSavingGoalUseCase, RetrieveSavingGoalUseCase,
        UpdateSavingGoalUseCase, RemoveSavingGoalUseCase {

    private static final Logger log = LoggerFactory.getLogger(SavingGoalService.class);

    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public SavingGoalService(SavingGoalRepositoryPort savingGoalRepositoryPort) {
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    @Override
    public SavingGoal addSavingGoal(SavingGoal savingGoal) {
        log.info("=== VALIDACIONES DE SERVICIO ===");

        validateNewGoal(savingGoal);

        log.info("=== TODAS LAS VALIDACIONES PASARON ===");

        SavingGoal goal = new SavingGoal(
                null,
                savingGoal.nombre(),
                savingGoal.montoObjetivo(),
                0,
                GoalStatus.EN_PROGRESO,
                savingGoal.fechaLimite(),
                savingGoal.titular());
        return savingGoalRepositoryPort.save(goal);
    }

    @Override
    public Optional<SavingGoal> findById(UUID id) {
        return savingGoalRepositoryPort.findById(id);
    }

    @Override
    public List<SavingGoal> findAll() {
        return savingGoalRepositoryPort.findAll();
    }

    @Override
    public SavingGoal updateSavingGoal(UUID goalId, SavingGoal savingGoal) {
        log.info("=== UPDATE SAVING GOAL ===");

        SavingGoal existingGoal = savingGoalRepositoryPort.findById(goalId)
                .orElseThrow(() -> new SavingGoalNotFoundException(
                        "Meta de ahorro no encontrada con ID: " + goalId));

        validateGoalPayload(savingGoal);
        validateUniqueNameOnUpdate(existingGoal, savingGoal.nombre());

        SavingGoal updatedGoal = new SavingGoal(
                goalId,
                savingGoal.nombre(),
                savingGoal.montoObjetivo(),
                existingGoal.avance(),
                existingGoal.estado(),
                savingGoal.fechaLimite(),
                existingGoal.titular());

        return savingGoalRepositoryPort.update(goalId, updatedGoal);
    }

    @Override
    public void deleteSavingGoalById(UUID goalId) {
        savingGoalRepositoryPort.findById(goalId)
                .orElseThrow(() -> new SavingGoalNotFoundException(
                        "Meta de ahorro no encontrada con ID: " + goalId));
        savingGoalRepositoryPort.deleteById(goalId);
    }

    private void validateNewGoal(SavingGoal savingGoal) {
        if (savingGoal == null) {
            throw new IllegalArgumentException("La meta no puede ser nula");
        }
        validateGoalPayload(savingGoal);
        if (savingGoal.titular() == null || savingGoal.titular().titularId() == null) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
        validateUniqueName(savingGoal.nombre());
    }

    private void validateGoalPayload(SavingGoal savingGoal) {
        if (isBlank(savingGoal.nombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (savingGoal.montoObjetivo() == null || savingGoal.montoObjetivo() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        validateFutureDate(savingGoal.fechaLimite());
    }

    private void validateUniqueNameOnUpdate(SavingGoal existingGoal, String newName) {
        if (!existingGoal.nombre().equals(newName)) {
            validateUniqueName(newName);
        }
    }

    private void validateUniqueName(String name) {
        if (savingGoalRepositoryPort.existsByNombre(name)) {
            throw new DuplicateGoalNameException("Ya existe una meta con el nombre: " + name);
        }
    }

    private void validateFutureDate(LocalDate fechaLimite) {
        if (fechaLimite != null && !fechaLimite.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite debe ser una fecha futura");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
