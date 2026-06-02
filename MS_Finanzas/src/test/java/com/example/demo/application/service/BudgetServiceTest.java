package com.example.demo.application.service;

import com.example.demo.application.repository.BudgetRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Budget;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.TypeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BudgetService")
class BudgetServiceTest {

    @Mock private BudgetRepositoryPort budgetRepositoryPort;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;
    @Mock private TitularRepositoryPort titularRepositoryPort;

    @InjectMocks private BudgetService budgetService;

    private UUID budgetId;
    private UUID titularId;
    private Titular titular;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private Budget budget;

    @BeforeEach
    void setUp() {
        budgetId = UUID.randomUUID();
        titularId = UUID.randomUUID();
        titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");
        fechaInicio = LocalDate.of(2026, 1, 1);
        fechaFinal = LocalDate.of(2026, 1, 31);
        budget = new Budget(budgetId, BigDecimal.valueOf(1_000_000), Instant.now(),
                fechaInicio, fechaFinal, BigDecimal.valueOf(200_000),
                BigDecimal.valueOf(800_000), titular);
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("retorna presupuesto recalculado cuando existe")
        void findById_existing_returnsRecalculated() {
            when(budgetRepositoryPort.findById(budgetId)).thenReturn(Optional.of(budget));
            when(transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
                    titularId, TypeTransaction.GASTO, fechaInicio, fechaFinal))
                    .thenReturn(BigDecimal.valueOf(300_000));

            Optional<Budget> result = budgetService.findById(budgetId);

            assertThat(result).isPresent();
            assertThat(result.get().gastoAcumulado()).isEqualByComparingTo("300000");
            assertThat(result.get().montoDisponible()).isEqualByComparingTo("700000");
        }

        @Test
        @DisplayName("retorna vacío cuando no existe")
        void findById_missing_returnsEmpty() {
            when(budgetRepositoryPort.findById(budgetId)).thenReturn(Optional.empty());
            assertThat(budgetService.findById(budgetId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("retorna lista recalculada")
        void findAll_returnsRecalculatedList() {
            when(budgetRepositoryPort.findAll()).thenReturn(List.of(budget));
            when(transactionRepositoryPort.sumByTitularAndTypeAndDateRange(any(), any(), any(), any()))
                    .thenReturn(BigDecimal.valueOf(100_000));

            List<Budget> result = budgetService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).gastoAcumulado()).isEqualByComparingTo("100000");
        }
    }

    @Nested
    @DisplayName("addBudget")
    class AddBudget {

        @Test
        @DisplayName("crea presupuesto cuando titular existe")
        void addBudget_titularExists_savesAndReturns() {
            Budget input = new Budget(null, BigDecimal.valueOf(1_000_000), null,
                    fechaInicio, fechaFinal, null, null, titular);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.of(titular));
            when(transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
                    titularId, TypeTransaction.GASTO, fechaInicio, fechaFinal))
                    .thenReturn(BigDecimal.valueOf(200_000));
            when(budgetRepositoryPort.save(any())).thenReturn(budget);

            Budget result = budgetService.addBudget(input, fechaInicio, fechaFinal);

            assertThat(result).isNotNull();
            verify(budgetRepositoryPort).save(any());
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si titular no existe")
        void addBudget_titularMissing_throwsResourceNotFoundException() {
            Budget input = new Budget(null, BigDecimal.valueOf(500_000), null,
                    fechaInicio, fechaFinal, null, null, titular);

            when(titularRepositoryPort.findById(titularId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> budgetService.addBudget(input, fechaInicio, fechaFinal))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(budgetRepositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateBudget")
    class UpdateBudget {

        @Test
        @DisplayName("actualiza presupuesto existente")
        void updateBudget_existing_updatesAndReturns() {
            Budget updatePayload = new Budget(null, BigDecimal.valueOf(2_000_000), null,
                    fechaInicio, fechaFinal, null, null, titular);

            when(budgetRepositoryPort.findById(budgetId)).thenReturn(Optional.of(budget));
            when(transactionRepositoryPort.sumByTitularAndTypeAndDateRange(any(), any(), any(), any()))
                    .thenReturn(BigDecimal.valueOf(500_000));
            when(budgetRepositoryPort.update(eq(budgetId), any())).thenReturn(updatePayload);

            Budget result = budgetService.updateBudget(budgetId, updatePayload);

            assertThat(result).isNotNull();
            verify(budgetRepositoryPort).update(eq(budgetId), any());
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si no existe")
        void updateBudget_missing_throwsResourceNotFoundException() {
            when(budgetRepositoryPort.findById(budgetId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> budgetService.updateBudget(budgetId, budget))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteBudget")
    class DeleteBudget {

        @Test
        @DisplayName("elimina presupuesto existente")
        void deleteBudget_existing_deletes() {
            when(budgetRepositoryPort.findById(budgetId)).thenReturn(Optional.of(budget));

            budgetService.deleteBudget(budgetId);

            verify(budgetRepositoryPort).deleteById(budgetId);
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si no existe")
        void deleteBudget_missing_throwsResourceNotFoundException() {
            when(budgetRepositoryPort.findById(budgetId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> budgetService.deleteBudget(budgetId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(budgetRepositoryPort, never()).deleteById(any());
        }
    }
}
