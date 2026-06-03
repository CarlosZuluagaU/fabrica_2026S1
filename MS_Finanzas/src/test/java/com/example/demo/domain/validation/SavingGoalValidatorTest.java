package com.example.demo.domain.validation;

import com.example.demo.domain.model.Titular;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SavingGoalValidatorTest {

    @Test
    void validateMonto_shouldAcceptPositive() {
        assertDoesNotThrow(() -> SavingGoalValidator.validateMonto(1.0));
    }

    @Test
    void validateMonto_shouldRejectZero() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateMonto(0.0));
    }

    @Test
    void validateMonto_shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateMonto(null));
    }

    @Test
    void validateMonto_shouldRejectNegative() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateMonto(-5.0));
    }

    @Test
    void validateNombre_shouldAcceptValidName() {
        assertDoesNotThrow(() -> SavingGoalValidator.validateNombre("Vacaciones"));
    }

    @Test
    void validateNombre_shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateNombre(null));
    }

    @Test
    void validateNombre_shouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateNombre("   "));
    }

    @Test
    void validateFechaLimite_shouldAcceptFutureDate() {
        assertDoesNotThrow(() -> SavingGoalValidator.validateFechaLimite(LocalDate.now().plusDays(1)));
    }

    @Test
    void validateFechaLimite_shouldRejectPastDate() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateFechaLimite(LocalDate.now().minusDays(1)));
    }

    @Test
    void validateFechaLimite_shouldRejectToday() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateFechaLimite(LocalDate.now()));
    }

    @Test
    void validateFechaLimite_shouldAcceptNull() {
        assertDoesNotThrow(() -> SavingGoalValidator.validateFechaLimite(null));
    }

    @Test
    void validateTitular_shouldAcceptValidTitular() {
        Titular t = new Titular(UUID.randomUUID(), "Ana", null, null, null, null, null, null, null);
        assertDoesNotThrow(() -> SavingGoalValidator.validateTitular(t));
    }

    @Test
    void validateTitular_shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateTitular(null));
    }

    @Test
    void validateTitular_shouldRejectTitularWithNullId() {
        Titular t = new Titular(null, "Ana", null, null, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> SavingGoalValidator.validateTitular(t));
    }
}
