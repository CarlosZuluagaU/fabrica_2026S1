package com.example.demo.infra.persistence.repository;

import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Budget;
import com.example.demo.domain.model.Titular;
import com.example.demo.infra.mapper.BudgetEntityMapper;
import com.example.demo.infra.persistence.entity.BudgetEntity;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaBudgetRepositoryAdapter")
class JpaBudgetRepositoryAdapterTest {

    @Mock private JpaBudgetRepository jpaBudgetRepository;
    @Mock private BudgetEntityMapper budgetEntityMapper;

    @InjectMocks private JpaBudgetRepositoryAdapter adapter;

    private UUID budgetId;
    private Budget budget;
    private BudgetEntity entity;

    @BeforeEach
    void setUp() {
        budgetId = UUID.randomUUID();
        UUID titularId = UUID.randomUUID();
        Titular titular = new Titular(titularId, "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");
        budget = new Budget(budgetId, BigDecimal.valueOf(1_000_000), Instant.now(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                BigDecimal.ZERO, BigDecimal.valueOf(1_000_000), titular);
        entity = new BudgetEntity();
        entity.setPresupuestoId(budgetId);
        entity.setMontoLimite(BigDecimal.valueOf(1_000_000));
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("retorna lista mapeada")
        void findAll_returnsMappedList() {
            when(jpaBudgetRepository.findAll()).thenReturn(List.of(entity));
            when(budgetEntityMapper.toDomain(entity)).thenReturn(budget);

            assertThat(adapter.findAll()).hasSize(1).contains(budget);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("retorna dominio cuando existe")
        void findById_existing_returnsDomain() {
            when(jpaBudgetRepository.findById(budgetId)).thenReturn(Optional.of(entity));
            when(budgetEntityMapper.toDomain(entity)).thenReturn(budget);

            assertThat(adapter.findById(budgetId)).isPresent().contains(budget);
        }

        @Test
        @DisplayName("retorna vacío cuando no existe")
        void findById_missing_returnsEmpty() {
            when(jpaBudgetRepository.findById(budgetId)).thenReturn(Optional.empty());
            assertThat(adapter.findById(budgetId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {
        @Test
        @DisplayName("guarda y retorna el mismo dominio")
        void save_persistsAndReturnsDomain() {
            when(budgetEntityMapper.toEntity(budget)).thenReturn(entity);
            when(jpaBudgetRepository.save(entity)).thenReturn(entity);

            Budget result = adapter.save(budget);

            assertThat(result).isEqualTo(budget);
            verify(jpaBudgetRepository).save(entity);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("actualiza campos y retorna dominio")
        void update_existing_updatesFields() {
            when(jpaBudgetRepository.findById(budgetId)).thenReturn(Optional.of(entity));
            when(jpaBudgetRepository.save(entity)).thenReturn(entity);

            Budget result = adapter.update(budgetId, budget);

            assertThat(result).isEqualTo(budget);
            verify(jpaBudgetRepository).save(entity);
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si no existe")
        void update_missing_throwsResourceNotFoundException() {
            when(jpaBudgetRepository.findById(budgetId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.update(budgetId, budget))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        @DisplayName("delega al repositorio JPA")
        void deleteById_delegatesToJpa() {
            adapter.deleteById(budgetId);
            verify(jpaBudgetRepository).deleteById(budgetId);
        }
    }

    @Nested
    @DisplayName("findByTitularAndDateRange")
    class FindByTitularAndDateRange {
        @Test
        @DisplayName("retorna lista filtrada")
        void findByTitularAndDateRange_returnsMappedList() {
            UUID titularId = budget.titular().titularId();
            LocalDate fecha = LocalDate.of(2026, 1, 15);
            when(jpaBudgetRepository.findByTitularAndDateRange(titularId, fecha))
                    .thenReturn(List.of(entity));
            when(budgetEntityMapper.toDomain(entity)).thenReturn(budget);

            assertThat(adapter.findByTitularAndDateRange(titularId, fecha))
                    .hasSize(1).contains(budget);
        }
    }
}
