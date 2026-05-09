package com.example.demo.infra.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.demo.domain.model.Category;
import com.example.demo.domain.model.Titular;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.rest.dto.TransactionRequest;
import com.example.demo.infra.rest.dto.SavingGoalRequest;
import com.example.demo.infra.persistence.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

class MapperDefaultMethodsExtraTest {

    @Test
    void transactionRequestMapper_toRequest_and_helpers() {
        TransactionRequestMapper mapper = new TransactionRequestMapper() {
            @Override
            public Transaction toDomain(TransactionRequest request) {
                return null; // not needed for this test
            }
        };

        // null transaction -> null request
        assertNull(mapper.toRequest(null));

        UUID catId = UUID.randomUUID();
        UUID titId = UUID.randomUUID();
        Transaction tx = new Transaction(UUID.randomUUID(), "name", "desc", BigDecimal.valueOf(12.5), TypeTransaction.GASTO,
                LocalDate.of(2024, 6, 1), new Category(catId, "c", null), new Titular(titId, "n", null, null, null, null, null, null, null));

        var req = mapper.toRequest(tx);
        assertNotNull(req);
        assertEquals(tx.nombre(), req.nombre());
        assertEquals(tx.monto(), req.monto());
        assertEquals(catId.toString(), req.categoriaId());
        assertEquals(titId.toString(), req.titularId());

        // mapCategory null/blank
        assertNull(mapper.mapCategory(null));
        assertNull(mapper.mapCategory("   "));

        Category c = mapper.mapCategory(catId.toString());
        assertNotNull(c);
        assertEquals(catId, c.categoriaId());

        Titular t = mapper.mapTitular(titId.toString());
        assertNotNull(t);
        assertEquals(titId, t.titularId());
    }

    @Test
    void transactionEntityMapper_stringToUuid() {
        TransactionEntityMapper m = new TransactionEntityMapper() {
            @Override
            public TransactionEntity toEntity(Transaction transaction) { return null; }

            @Override
            public Transaction toDomain(TransactionEntity transactionEntity) { return null; }
        };

        assertNull(m.stringToUuid(null));
        UUID id = UUID.randomUUID();
        assertEquals(id, m.stringToUuid(id.toString()));
    }

    @Test
    void savingGoalRequestMapper_createTitular_behaviour() {
        SavingGoalRequestMapper mapper = new SavingGoalRequestMapper() {
            @Override
            public com.example.demo.domain.model.SavingGoal toDomain(SavingGoalRequest request) { return null; }
        };

        // when titularId is null -> exception
        SavingGoalRequest req = new SavingGoalRequest(null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> mapper.createTitular(req));

        UUID tid = UUID.randomUUID();
        SavingGoalRequest req2 = new SavingGoalRequest(null, null, null, tid);
        var titular = mapper.createTitular(req2);
        assertNotNull(titular);
        assertEquals(tid, titular.titularId());
    }
}
