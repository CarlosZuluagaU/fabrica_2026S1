package com.example.demo.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.repository.BudgetRepositoryPort;
import com.example.demo.application.repository.CategoryRepositoryPort;
import com.example.demo.application.repository.TitularRepositoryPort;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.EmptyCategoryConstants;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private TitularRepositoryPort titularRepositoryPort;

    @Mock
    private BudgetRepositoryPort budgetRepositoryPort;

    @InjectMocks
    private TransactionService transactionService;

    private UUID titularId;
    private UUID categoryId;
    private Titular titular;
    private Category category;

    @BeforeEach
    void setUp() {
        titularId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        titular = new Titular(titularId, "Luis", "Martínez", "Lopez", "3214567890", null, "USD", "America/New_York", "token");
        category = new Category(categoryId, "Taxi", titular);
    }

    @Test
    void createTransaction_shouldSaveTransactionWithExistingCategory() {
        Transaction partial = new Transaction(null, "Viaje", "Taxi aeropuerto", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        Transaction saved = new Transaction(UUID.randomUUID(), "Viaje", "Taxi aeropuerto", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.of(category));
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.createTransaction(partial);

        assertEquals(saved, result);
        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void createTransaction_shouldCreateEmptyCategoryWhenNoCategoryProvided() {
        Transaction partial = new Transaction(null, "Pago", "Pago sin categoría", BigDecimal.ONE, TypeTransaction.INGRESO, null, null, titular);
        Category emptyCategory = new Category(null, EmptyCategoryConstants.NAME, null);
        Transaction saved = new Transaction(UUID.randomUUID(), "Pago", "Pago sin categoría", BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(), emptyCategory, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findByNombreIgnoreCase(EmptyCategoryConstants.NAME)).willReturn(Optional.empty());
        given(categoryRepositoryPort.save(any(Category.class))).willReturn(emptyCategory);
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.createTransaction(partial);

        assertEquals(saved, result);
        verify(categoryRepositoryPort).save(any(Category.class));
        verify(transactionRepositoryPort).save(any(Transaction.class));
    }

    @Test
    void createTransaction_shouldThrowWhenTitularNotFound() {
        Transaction partial = new Transaction(null, "Pago", "Sin titular", BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(), category, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(partial));
    }

    @Test
    void updateTransaction_shouldThrowWhenTransactionDoesNotExist() {
        UUID transactionId = UUID.randomUUID();
        Transaction partial = new Transaction(null, "Viaje", "Taxi", BigDecimal.valueOf(15000), TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(transactionRepositoryPort.findById(transactionId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(transactionId, partial));
    }

    @Test
    void updateTransaction_shouldUpdateWhenExists() {
        UUID transactionId = UUID.randomUUID();
        Transaction existing = new Transaction(transactionId, "Viaje", "Taxi", BigDecimal.valueOf(15000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        Transaction partial = new Transaction(null, "Viaje 2", "Taxi 2", BigDecimal.valueOf(20000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        Transaction saved = new Transaction(transactionId, "Viaje 2", "Taxi 2", BigDecimal.valueOf(20000), TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(transactionRepositoryPort.findById(transactionId)).willReturn(Optional.of(existing));
        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.of(category));
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.updateTransaction(transactionId, partial);

        assertEquals(saved, result);
    }

    @Test
    void findAll_shouldReturnFilteredList() {
        com.example.demo.application.query.TransactionListFilter filter = com.example.demo.application.query.TransactionListFilter.none();
        Transaction tx = new Transaction(UUID.randomUUID(), "Viaje", "Taxi", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        given(transactionRepositoryPort.findAll(filter)).willReturn(java.util.List.of(tx));

        assertEquals(1, transactionService.findAll(filter).size());
    }

    @Test
    void findById_shouldReturnTransactionWhenExists() {
        UUID transactionId = UUID.randomUUID();
        Transaction tx = new Transaction(transactionId, "Viaje", "Taxi", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        given(transactionRepositoryPort.findById(transactionId)).willReturn(Optional.of(tx));

        assertEquals(Optional.of(tx), transactionService.findById(transactionId));
    }

    @Test
    void deleteTransaction_shouldCallRepository() {
        UUID transactionId = UUID.randomUUID();

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepositoryPort).deleteById(transactionId);
    }

    @Test
    void findFiltered_shouldBuildFilterAndDelegate() {
        Transaction tx = new Transaction(UUID.randomUUID(), "Viaje", "Taxi", BigDecimal.valueOf(12000), TypeTransaction.GASTO, LocalDate.now(), category, titular);
        given(transactionRepositoryPort.findAll(any())).willReturn(java.util.List.of(tx));

        java.util.List<Transaction> result = transactionService.findFiltered(TypeTransaction.GASTO, categoryId, titularId, LocalDate.now(), LocalDate.now());

        assertEquals(1, result.size());
        verify(transactionRepositoryPort).findAll(any());
    }

    @Test
    void createTransaction_shouldCreateEmptyCategoryWhenItAlreadyExists() {
        Category emptyCategory = new Category(UUID.randomUUID(), EmptyCategoryConstants.NAME, null);
        Transaction partial = new Transaction(null, "Pago", "Pago sin categoría", BigDecimal.ONE, TypeTransaction.INGRESO, null, null, titular);
        Transaction saved = new Transaction(UUID.randomUUID(), "Pago", "Pago sin categoría", BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(), emptyCategory, titular);

        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findByNombreIgnoreCase(EmptyCategoryConstants.NAME)).willReturn(Optional.of(emptyCategory));
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.createTransaction(partial);

        assertEquals(saved, result);
    }

    @Test
    void createTransaction_shouldThrowWhenBudgetExceeded() {
        com.example.demo.domain.model.Budget budget = new com.example.demo.domain.model.Budget(
            UUID.randomUUID(), BigDecimal.valueOf(10000), java.time.Instant.now(),
            LocalDate.now().minusDays(5), LocalDate.now().plusDays(25),
            BigDecimal.ZERO, BigDecimal.valueOf(10000), titular
        );
        Transaction partial = new Transaction(null, "Compra cara", "desc", BigDecimal.valueOf(15000),
            TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(budgetRepositoryPort.findByTitularAndDateRange(titularId, LocalDate.now()))
            .willReturn(java.util.List.of(budget));
        given(transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
            titularId, TypeTransaction.GASTO, budget.fechaInicio(), budget.fechaFinal()))
            .willReturn(BigDecimal.valueOf(9000));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(partial));
    }

    @Test
    void createTransaction_shouldSucceedWhenBudgetNotExceeded() {
        com.example.demo.domain.model.Budget budget = new com.example.demo.domain.model.Budget(
            UUID.randomUUID(), BigDecimal.valueOf(50000), java.time.Instant.now(),
            LocalDate.now().minusDays(5), LocalDate.now().plusDays(25),
            BigDecimal.ZERO, BigDecimal.valueOf(50000), titular
        );
        Transaction partial = new Transaction(null, "Compra", "desc", BigDecimal.valueOf(1000),
            TypeTransaction.GASTO, LocalDate.now(), category, titular);
        Transaction saved = new Transaction(UUID.randomUUID(), "Compra", "desc", BigDecimal.valueOf(1000),
            TypeTransaction.GASTO, LocalDate.now(), category, titular);

        given(budgetRepositoryPort.findByTitularAndDateRange(titularId, LocalDate.now()))
            .willReturn(java.util.List.of(budget));
        given(transactionRepositoryPort.sumByTitularAndTypeAndDateRange(
            titularId, TypeTransaction.GASTO, budget.fechaInicio(), budget.fechaFinal()))
            .willReturn(BigDecimal.valueOf(5000));
        given(titularRepositoryPort.findById(titularId)).willReturn(Optional.of(titular));
        given(categoryRepositoryPort.findById(categoryId)).willReturn(Optional.of(category));
        given(transactionRepositoryPort.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.createTransaction(partial);

        assertEquals(saved, result);
    }
}
