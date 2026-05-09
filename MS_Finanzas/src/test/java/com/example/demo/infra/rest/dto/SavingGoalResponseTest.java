package com.example.demo.infra.rest.dto;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.model.GoalStatus;

class SavingGoalResponseTest {

    @Test
    void getPorcentajeAvance_shouldReturnZeroWhenMontoNotPositive() {
        SavingGoalResponse response = new SavingGoalResponse(UUID.randomUUID(), "Vacaciones", 0.0, 0.0, GoalStatus.EN_PROGRESO.name(), LocalDate.now(), UUID.randomUUID(), "Ana");

        assertThat(response.getPorcentajeAvance()).isEqualTo(0.0);
    }

    @Test
    void getPorcentajeAvance_shouldCalculateCorrectly() {
        SavingGoalResponse response = new SavingGoalResponse(UUID.randomUUID(), "Vacaciones", 1000.0, 500.0, GoalStatus.EN_PROGRESO.name(), LocalDate.now(), UUID.randomUUID(), "Ana");

        assertThat(response.getPorcentajeAvance()).isEqualTo(50.0);
    }
}
