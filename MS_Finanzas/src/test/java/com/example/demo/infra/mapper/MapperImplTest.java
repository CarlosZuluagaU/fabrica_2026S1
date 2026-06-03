package com.example.demo.infra.mapper;

import com.example.demo.domain.model.*;
import com.example.demo.infra.persistence.entity.*;
import com.example.demo.infra.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapperImplTest {

    // ── helpers ───────────────────────────────────────────────────────────

    private TitularEntity titularEntity() {
        TitularEntity e = new TitularEntity();
        e.setTitularId(UUID.randomUUID());
        e.setNombre("Ana");
        e.setPrimerApellido("Lopez");
        e.setSegundoApellido("Garcia");
        e.setTelefono("3001234567");
        e.setFechaRegistro(Instant.now());
        e.setMonedaPreferida("COP");
        e.setZonaHoraria("America/Bogota");
        e.setToken("tkn");
        return e;
    }

    private Titular titular() {
        return new Titular(UUID.randomUUID(), "Ana", "Lopez", "Garcia",
                "3001234567", Instant.now(), "COP", "America/Bogota", "tkn");
    }

    // ── TitularEntityMapper ───────────────────────────────────────────────

    @Test
    void titularEntityMapper_toDomain_null_returnsNull() {
        assertThat(new TitularEntityMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void titularEntityMapper_toDomain_mapsAllFields() {
        TitularEntity e = titularEntity();
        Titular d = new TitularEntityMapperImpl().toDomain(e);
        assertThat(d.titularId()).isEqualTo(e.getTitularId());
        assertThat(d.nombre()).isEqualTo(e.getNombre());
        assertThat(d.token()).isEqualTo(e.getToken());
    }

    @Test
    void titularEntityMapper_toEntity_null_returnsNull() {
        assertThat(new TitularEntityMapperImpl().toEntity(null)).isNull();
    }

    @Test
    void titularEntityMapper_toEntity_mapsAllFields() {
        Titular t = titular();
        TitularEntity e = new TitularEntityMapperImpl().toEntity(t);
        assertThat(e.getTitularId()).isEqualTo(t.titularId());
        assertThat(e.getNombre()).isEqualTo(t.nombre());
        assertThat(e.getToken()).isEqualTo(t.token());
    }

    // ── CategoryEntityMapper ──────────────────────────────────────────────

    @Test
    void categoryEntityMapper_toDomain_null_returnsNull() {
        assertThat(new CategoryEntityMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void categoryEntityMapper_toDomain_withTitular() {
        CategoryEntity e = new CategoryEntity();
        e.setCategoriaId(UUID.randomUUID());
        e.setNombre("Alimentación");
        e.setTitular(titularEntity());

        Category d = new CategoryEntityMapperImpl().toDomain(e);
        assertThat(d.categoriaId()).isEqualTo(e.getCategoriaId());
        assertThat(d.nombre()).isEqualTo("Alimentación");
        assertThat(d.titular()).isNotNull();
    }

    @Test
    void categoryEntityMapper_toDomain_withNullTitular() {
        CategoryEntity e = new CategoryEntity();
        e.setCategoriaId(UUID.randomUUID());
        e.setNombre("Salud");

        Category d = new CategoryEntityMapperImpl().toDomain(e);
        assertThat(d.titular()).isNull();
    }

    @Test
    void categoryEntityMapper_toEntity_null_returnsNull() {
        assertThat(new CategoryEntityMapperImpl().toEntity(null)).isNull();
    }

    @Test
    void categoryEntityMapper_toEntity_withTitular() {
        Category c = new Category(UUID.randomUUID(), "Transporte", titular());
        CategoryEntity e = new CategoryEntityMapperImpl().toEntity(c);
        assertThat(e.getCategoriaId()).isEqualTo(c.categoriaId());
        assertThat(e.getNombre()).isEqualTo("Transporte");
        assertThat(e.getTitular()).isNotNull();
    }

    @Test
    void categoryEntityMapper_toEntity_withNullTitular() {
        Category c = new Category(UUID.randomUUID(), "Sin titular", null);
        CategoryEntity e = new CategoryEntityMapperImpl().toEntity(c);
        assertThat(e.getTitular()).isNull();
    }

    // ── BudgetEntityMapper ────────────────────────────────────────────────

    @Test
    void budgetEntityMapper_toDomain_null_returnsNull() {
        assertThat(new BudgetEntityMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void budgetEntityMapper_toDomain_withTitular() {
        BudgetEntity e = new BudgetEntity();
        e.setPresupuestoId(UUID.randomUUID());
        e.setMontoLimite(BigDecimal.valueOf(1_000_000));
        e.setFechaCreacion(Instant.now());
        e.setFechaInicio(LocalDate.now());
        e.setFechaFinal(LocalDate.now().plusMonths(1));
        e.setTitular(titularEntity());

        Budget d = new BudgetEntityMapperImpl().toDomain(e);
        assertThat(d.presupuestoId()).isEqualTo(e.getPresupuestoId());
        assertThat(d.montoLimite()).isEqualByComparingTo("1000000");
        assertThat(d.titular()).isNotNull();
    }

    @Test
    void budgetEntityMapper_toDomain_withNullTitular() {
        BudgetEntity e = new BudgetEntity();
        e.setPresupuestoId(UUID.randomUUID());
        e.setMontoLimite(BigDecimal.TEN);
        e.setFechaCreacion(Instant.now());
        e.setFechaInicio(LocalDate.now());

        Budget d = new BudgetEntityMapperImpl().toDomain(e);
        assertThat(d.titular()).isNull();
    }

    @Test
    void budgetEntityMapper_toEntity_null_returnsNull() {
        assertThat(new BudgetEntityMapperImpl().toEntity(null)).isNull();
    }

    @Test
    void budgetEntityMapper_toEntity_withTitular() {
        Budget b = new Budget(UUID.randomUUID(), BigDecimal.valueOf(500_000),
                Instant.now(), LocalDate.now(), LocalDate.now().plusDays(30),
                BigDecimal.ZERO, BigDecimal.valueOf(500_000), titular());

        BudgetEntity e = new BudgetEntityMapperImpl().toEntity(b);
        assertThat(e.getPresupuestoId()).isEqualTo(b.presupuestoId());
        assertThat(e.getTitular()).isNotNull();
    }

    @Test
    void budgetEntityMapper_toEntity_withNullTitular() {
        Budget b = new Budget(UUID.randomUUID(), BigDecimal.TEN,
                Instant.now(), LocalDate.now(), LocalDate.now().plusDays(30),
                BigDecimal.ZERO, BigDecimal.TEN, null);

        BudgetEntity e = new BudgetEntityMapperImpl().toEntity(b);
        assertThat(e.getTitular()).isNull();
    }

    // ── SavingGoalEntityMapper ────────────────────────────────────────────

    @Test
    void savingGoalEntityMapper_toDomain_null_returnsNull() {
        assertThat(new SavingGoalEntityMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void savingGoalEntityMapper_toDomain_withTitular() {
        SavingGoalEntity e = new SavingGoalEntity();
        e.setGoalId(UUID.randomUUID());
        e.setNombre("Vacaciones");
        e.setMontoObjetivo(2_000_000.0);
        e.setAvance(500_000);
        e.setEstado(GoalStatus.EN_PROGRESO);
        e.setFechaLimite(LocalDate.now().plusMonths(6));
        e.setTitular(titularEntity());

        SavingGoal d = new SavingGoalEntityMapperImpl().toDomain(e);
        assertThat(d.goalId()).isEqualTo(e.getGoalId());
        assertThat(d.nombre()).isEqualTo("Vacaciones");
        assertThat(d.titular()).isNotNull();
    }

    @Test
    void savingGoalEntityMapper_toDomain_withNullTitular() {
        SavingGoalEntity e = new SavingGoalEntity();
        e.setGoalId(UUID.randomUUID());
        e.setNombre("Carro");
        e.setMontoObjetivo(10_000_000.0);
        e.setAvance(0);
        e.setEstado(GoalStatus.EN_PROGRESO);

        SavingGoal d = new SavingGoalEntityMapperImpl().toDomain(e);
        assertThat(d.titular()).isNull();
    }

    @Test
    void savingGoalEntityMapper_toEntity_null_returnsNull() {
        assertThat(new SavingGoalEntityMapperImpl().toEntity(null)).isNull();
    }

    @Test
    void savingGoalEntityMapper_toEntity_withTitular() {
        SavingGoal g = new SavingGoal(UUID.randomUUID(), "Meta", 1000.0, 0,
                GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(3), titular());

        SavingGoalEntity e = new SavingGoalEntityMapperImpl().toEntity(g);
        assertThat(e.getGoalId()).isEqualTo(g.goalId());
        assertThat(e.getNombre()).isEqualTo("Meta");
        assertThat(e.getTitular()).isNotNull();
    }

    @Test
    void savingGoalEntityMapper_toEntity_withNullTitular() {
        SavingGoal g = new SavingGoal(UUID.randomUUID(), "Meta", 1000.0, 0,
                GoalStatus.EN_PROGRESO, null, null);

        SavingGoalEntity e = new SavingGoalEntityMapperImpl().toEntity(g);
        assertThat(e.getTitular()).isNull();
    }

    // ── TransactionEntityMapper ───────────────────────────────────────────

    @Test
    void transactionEntityMapper_toDomain_null_returnsNull() {
        assertThat(new TransactionEntityMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void transactionEntityMapper_toDomain_withCategoryAndTitular() {
        TransactionEntity e = new TransactionEntity();
        e.setTransactionId(UUID.randomUUID());
        e.setNombre("Supermercado");
        e.setDescripcion("Compras del mes");
        e.setMonto(BigDecimal.valueOf(85_000));
        e.setTipo(TypeTransaction.GASTO);
        e.setFecha(LocalDate.now());
        CategoryEntity cat = new CategoryEntity();
        cat.setCategoriaId(UUID.randomUUID());
        cat.setNombre("Alimentación");
        cat.setTitular(titularEntity());
        e.setCategoria(cat);
        e.setTitular(titularEntity());

        Transaction d = new TransactionEntityMapperImpl().toDomain(e);
        assertThat(d.transactionId()).isEqualTo(e.getTransactionId());
        assertThat(d.nombre()).isEqualTo("Supermercado");
        assertThat(d.categoria()).isNotNull();
        assertThat(d.titular()).isNotNull();
    }

    @Test
    void transactionEntityMapper_toDomain_withNullCategoryAndTitular() {
        TransactionEntity e = new TransactionEntity();
        e.setTransactionId(UUID.randomUUID());
        e.setNombre("Ingreso");
        e.setMonto(BigDecimal.valueOf(1_000_000));
        e.setTipo(TypeTransaction.INGRESO);
        e.setFecha(LocalDate.now());

        Transaction d = new TransactionEntityMapperImpl().toDomain(e);
        assertThat(d.categoria()).isNull();
        assertThat(d.titular()).isNull();
    }

    @Test
    void transactionEntityMapper_toEntity_null_returnsNull() {
        assertThat(new TransactionEntityMapperImpl().toEntity(null)).isNull();
    }

    @Test
    void transactionEntityMapper_toEntity_mapsScalarFields() {
        Titular t = titular();
        Category c = new Category(UUID.randomUUID(), "Taxi", t);
        Transaction tx = new Transaction(UUID.randomUUID(), "Taxi", "Viaje", BigDecimal.valueOf(25_000),
                TypeTransaction.GASTO, LocalDate.now(), c, t);

        TransactionEntity e = new TransactionEntityMapperImpl().toEntity(tx);
        assertThat(e.getTransactionId()).isEqualTo(tx.transactionId());
        assertThat(e.getNombre()).isEqualTo("Taxi");
        assertThat(e.getMonto()).isEqualByComparingTo("25000");
    }

    // ── ReportEntityMapper ────────────────────────────────────────────────

    @Test
    void reportEntityMapper_toDomain_bothNull_returnsNull() {
        assertThat(new ReportEntityMapperImpl().toDomain(null, null)).isNull();
    }

    @Test
    void reportEntityMapper_toDomain_withBothEntities() {
        ReportEntity re = new ReportEntity();
        re.setReportId(UUID.randomUUID());
        re.setMes(5);
        re.setAnho(2026);
        re.setIngresosAcumulados(BigDecimal.valueOf(3_000_000));
        re.setGastosAcumulados(BigDecimal.valueOf(1_500_000));
        re.setAportesMetaAcumulados(BigDecimal.valueOf(500_000));
        re.setBalanceNeto(BigDecimal.valueOf(1_000_000));
        re.setFechaGenerado(Instant.now());

        TitularEntity te = titularEntity();
        Report d = new ReportEntityMapperImpl().toDomain(re, te);
        assertThat(d.reportId()).isEqualTo(re.getReportId());
        assertThat(d.mes()).isEqualTo(5);
        assertThat(d.titular()).isNotNull();
    }

    @Test
    void reportEntityMapper_toDomain_reportNullTitularNotNull() {
        TitularEntity te = titularEntity();
        Report d = new ReportEntityMapperImpl().toDomain(null, te);
        assertThat(d).isNotNull();
        assertThat(d.titular()).isNotNull();
        assertThat(d.reportId()).isNull();
    }

    @Test
    void reportEntityMapper_toEntity_null_returnsNull() {
        assertThat(new ReportEntityMapperImpl().toEntity(null)).isNull();
    }

    @Test
    void reportEntityMapper_toEntity_withTitular() {
        Report r = new Report(UUID.randomUUID(), 5, 2026,
                BigDecimal.valueOf(3_000_000), BigDecimal.valueOf(1_500_000),
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(1_000_000),
                Instant.now(), titular());

        ReportEntity e = new ReportEntityMapperImpl().toEntity(r);
        assertThat(e.getReportId()).isEqualTo(r.reportId());
        assertThat(e.getMes()).isEqualTo(5);
        assertThat(e.getTitular()).isNotNull();
    }

    @Test
    void reportEntityMapper_toEntity_withNullTitular() {
        Report r = new Report(UUID.randomUUID(), 6, 2026,
                BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO,
                Instant.now(), null);

        ReportEntity e = new ReportEntityMapperImpl().toEntity(r);
        assertThat(e.getTitular()).isNull();
    }

    // ── BudgetRequestMapper ───────────────────────────────────────────────

    @Test
    void budgetRequestMapper_toDomain_null_returnsNull() {
        assertThat(new BudgetRequestMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void budgetRequestMapper_toDomain_mapsFields() {
        UUID titularId = UUID.randomUUID();
        BudgetRequest req = new BudgetRequest(
                BigDecimal.valueOf(500_000),
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                titularId.toString());

        Budget d = new BudgetRequestMapperImpl().toDomain(req);
        assertThat(d.montoLimite()).isEqualByComparingTo("500000");
        assertThat(d.titular()).isNotNull();
        assertThat(d.titular().titularId()).isEqualTo(titularId);
    }

    // ── BudgetResponseMapper ──────────────────────────────────────────────

    @Test
    void budgetResponseMapper_toResponse_null_returnsNull() {
        assertThat(new BudgetResponseMapperImpl().toResponse(null)).isNull();
    }

    @Test
    void budgetResponseMapper_toResponse_mapsFields() {
        Budget b = new Budget(UUID.randomUUID(), BigDecimal.valueOf(1_000_000),
                Instant.now(), LocalDate.now(), LocalDate.now().plusMonths(1),
                BigDecimal.valueOf(200_000), BigDecimal.valueOf(800_000), null);

        BudgetResponse r = new BudgetResponseMapperImpl().toResponse(b);
        assertThat(r.presupuestoId()).isEqualTo(b.presupuestoId());
        assertThat(r.montoLimite()).isEqualByComparingTo("1000000");
        assertThat(r.gastoAcumulado()).isEqualByComparingTo("200000");
    }

    // ── CategoryRequestMapper ─────────────────────────────────────────────

    @Test
    void categoryRequestMapper_toDomain_null_returnsNull() {
        assertThat(new CategoryRequestMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void categoryRequestMapper_toDomain_mapsFields() {
        UUID titularId = UUID.randomUUID();
        CategoryRequest req = new CategoryRequest("Salud", titularId);

        Category d = new CategoryRequestMapperImpl().toDomain(req);
        assertThat(d.nombre()).isEqualTo("Salud");
        assertThat(d.titular().titularId()).isEqualTo(titularId);
    }

    // ── CategoryResponseMapper ────────────────────────────────────────────

    @Test
    void categoryResponseMapper_toResponse_null_returnsNull() {
        assertThat(new CategoryResponseMapperImpl().toResponse(null)).isNull();
    }

    @Test
    void categoryResponseMapper_toResponse_mapsNombre() {
        Category c = new Category(UUID.randomUUID(), "Transporte", null);
        CategoryResponse r = new CategoryResponseMapperImpl().toResponse(c);
        assertThat(r.nombre()).isEqualTo("Transporte");
    }

    // ── TitularRequestMapper ──────────────────────────────────────────────

    @Test
    void titularRequestMapper_toDomain_null_returnsNull() {
        assertThat(new TitularRequestMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void titularRequestMapper_toDomain_mapsFields() {
        TitularRequest req = new TitularRequest("Luis", "Martínez", "Lopez",
                "3219998877", "USD", "America/New_York");

        Titular d = new TitularRequestMapperImpl().toDomain(req);
        assertThat(d.nombre()).isEqualTo("Luis");
        assertThat(d.monedaPreferida()).isEqualTo("USD");
        assertThat(d.zonaHoraria()).isEqualTo("America/New_York");
        assertThat(d.titularId()).isNull();
    }

    // ── TitularResponseMapper ─────────────────────────────────────────────

    @Test
    void titularResponseMapper_toResponse_null_returnsNull() {
        assertThat(new TitularResponseMapperImpl().toResponse(null)).isNull();
    }

    @Test
    void titularResponseMapper_toResponse_mapsFields() {
        Titular t = titular();
        TitularResponse r = new TitularResponseMapperImpl().toResponse(t);
        assertThat(r.titularId()).isEqualTo(t.titularId());
        assertThat(r.nombre()).isEqualTo(t.nombre());
        assertThat(r.monedaPreferida()).isEqualTo(t.monedaPreferida());
    }

    // ── SavingGoalRequestMapper ───────────────────────────────────────────

    @Test
    void savingGoalRequestMapper_toDomain_null_returnsNull() {
        assertThat(new SavingGoalRequestMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void savingGoalRequestMapper_toDomain_withTitularId() {
        UUID titularId = UUID.randomUUID();
        SavingGoalRequest req = new SavingGoalRequest("Vacaciones", 2_000_000.0,
                LocalDate.now().plusMonths(6), titularId);

        SavingGoal d = new SavingGoalRequestMapperImpl().toDomain(req);
        assertThat(d.nombre()).isEqualTo("Vacaciones");
        assertThat(d.titular().titularId()).isEqualTo(titularId);
    }

    // ── SavingGoalResponseMapper ──────────────────────────────────────────

    @Test
    void savingGoalResponseMapper_toResponse_null_returnsNull() {
        assertThat(new SavingGoalResponseMapperImpl().toResponse(null)).isNull();
    }

    @Test
    void savingGoalResponseMapper_toResponse_withTitular() {
        Titular t = titular();
        SavingGoal g = new SavingGoal(UUID.randomUUID(), "Carro", 10_000_000.0, 500_000,
                GoalStatus.EN_PROGRESO, LocalDate.now().plusMonths(12), t);

        SavingGoalResponse r = new SavingGoalResponseMapperImpl().toResponse(g);
        assertThat(r.getGoalId()).isEqualTo(g.goalId());
        assertThat(r.getNombre()).isEqualTo("Carro");
        assertThat(r.getTitularId()).isEqualTo(t.titularId());
        assertThat(r.getEstado()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void savingGoalResponseMapper_toResponse_withNullTitular() {
        SavingGoal g = new SavingGoal(UUID.randomUUID(), "Carro", 5_000_000.0, 0,
                GoalStatus.EN_PROGRESO, null, null);

        SavingGoalResponse r = new SavingGoalResponseMapperImpl().toResponse(g);
        assertThat(r.getTitularId()).isNull();
        assertThat(r.getTitularNombre()).isNull();
    }

    // ── ReportRequestMapper ───────────────────────────────────────────────

    @Test
    void reportRequestMapper_toDomain_null_returnsNull() {
        assertThat(new ReportRequestMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void reportRequestMapper_toDomain_withTitularId() {
        UUID titularId = UUID.randomUUID();
        ReportRequest req = new ReportRequest(5, 2026, titularId);

        Report d = new ReportRequestMapperImpl().toDomain(req);
        assertThat(d.mes()).isEqualTo(5);
        assertThat(d.anho()).isEqualTo(2026);
        assertThat(d.titular().titularId()).isEqualTo(titularId);
    }

    @Test
    void reportRequestMapper_toDomain_withNullTitularId() {
        ReportRequest req = new ReportRequest(1, 2025, null);
        Report d = new ReportRequestMapperImpl().toDomain(req);
        assertThat(d.titular()).isNull();
    }

    // ── ReportResponseMapper ──────────────────────────────────────────────

    @Test
    void reportResponseMapper_toResponse_null_returnsNull() {
        assertThat(new ReportResponseMapperImpl().toResponse(null)).isNull();
    }

    @Test
    void reportResponseMapper_toResponse_mapsFields() {
        Report r = new Report(UUID.randomUUID(), 3, 2026,
                BigDecimal.valueOf(2_000_000), BigDecimal.valueOf(800_000),
                BigDecimal.valueOf(200_000), BigDecimal.valueOf(1_000_000),
                Instant.now(), titular());

        ReportResponse resp = new ReportResponseMapperImpl().toResponse(r);
        assertThat(resp.reportId()).isEqualTo(r.reportId());
        assertThat(resp.mes()).isEqualTo(3);
        assertThat(resp.balanceNeto()).isEqualByComparingTo("1000000");
    }

    // ── TransactionRequestMapper ──────────────────────────────────────────

    @Test
    void transactionRequestMapper_toDomain_null_returnsNull() {
        assertThat(new TransactionRequestMapperImpl().toDomain(null)).isNull();
    }

    @Test
    void transactionRequestMapper_toDomain_withCategoriaId() {
        UUID titularId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        TransactionRequest req = new TransactionRequest("Taxi", BigDecimal.valueOf(25_000),
                "Viaje", TypeTransaction.GASTO, LocalDate.now(),
                catId.toString(), titularId.toString());

        Transaction d = new TransactionRequestMapperImpl().toDomain(req);
        assertThat(d.nombre()).isEqualTo("Taxi");
        assertThat(d.categoria().categoriaId()).isEqualTo(catId);
        assertThat(d.titular().titularId()).isEqualTo(titularId);
    }

    @Test
    void transactionRequestMapper_toDomain_withNullCategoriaId() {
        UUID titularId = UUID.randomUUID();
        TransactionRequest req = new TransactionRequest("Ingreso", BigDecimal.valueOf(1_000_000),
                "Salario", TypeTransaction.INGRESO, LocalDate.now(),
                null, titularId.toString());

        Transaction d = new TransactionRequestMapperImpl().toDomain(req);
        assertThat(d.categoria()).isNull();
        assertThat(d.titular().titularId()).isEqualTo(titularId);
    }

    // ── TransactionResponseMapper ─────────────────────────────────────────

    @Test
    void transactionResponseMapper_toResponse_null_returnsNull() {
        assertThat(new TransactionResponseMapperImpl().toResponse(null)).isNull();
    }

    @Test
    void transactionResponseMapper_toResponse_withCategoryAndTitular() {
        Titular t = titular();
        Category c = new Category(UUID.randomUUID(), "Salud", t);
        Transaction tx = new Transaction(UUID.randomUUID(), "Farmacia", "Medicamentos",
                BigDecimal.valueOf(45_000), TypeTransaction.GASTO, LocalDate.now(), c, t);

        TransactionResponse r = new TransactionResponseMapperImpl().toResponse(tx);
        assertThat(r.transactionId()).isEqualTo(tx.transactionId());
        assertThat(r.nombreCategoria()).isEqualTo("Salud");
        assertThat(r.nombreTitular()).isEqualTo("Ana");
    }

    @Test
    void transactionResponseMapper_toResponse_withNullCategoryAndTitular() {
        Transaction tx = new Transaction(UUID.randomUUID(), "Ingreso", "desc",
                BigDecimal.ONE, TypeTransaction.INGRESO, LocalDate.now(), null, null);

        TransactionResponse r = new TransactionResponseMapperImpl().toResponse(tx);
        assertThat(r.nombreCategoria()).isNull();
        assertThat(r.nombreTitular()).isNull();
    }
}
