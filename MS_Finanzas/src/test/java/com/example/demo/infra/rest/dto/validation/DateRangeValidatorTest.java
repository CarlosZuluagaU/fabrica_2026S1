package com.example.demo.infra.rest.dto.validation;

import com.example.demo.infra.rest.dto.BudgetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DateRangeValidatorTest {

    private DateRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator();
    }

    private BudgetRequest request(LocalDate inicio, LocalDate fin) {
        return new BudgetRequest(BigDecimal.valueOf(1000), inicio, fin, "00000000-0000-0000-0000-000000000001");
    }

    @Test
    void isValid_shouldReturnTrueWhenNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void isValid_shouldReturnTrueWhenFechaFinalEqualsInicio() {
        LocalDate today = LocalDate.now();
        assertThat(validator.isValid(request(today, today), null)).isTrue();
    }

    @Test
    void isValid_shouldReturnTrueWhenFechaFinalAfterInicio() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(10);
        assertThat(validator.isValid(request(inicio, fin), null)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalseWhenFechaFinalBeforeInicio() {
        LocalDate inicio = LocalDate.now().plusDays(5);
        LocalDate fin = LocalDate.now();
        assertThat(validator.isValid(request(inicio, fin), null)).isFalse();
    }
}
