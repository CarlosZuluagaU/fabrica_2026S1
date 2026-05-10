package com.example.demo.infra.persistence.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.application.query.TransactionListFilter;
import com.example.demo.application.repository.TransactionRepositoryPort;
import com.example.demo.domain.exception.ResourceNotFoundException;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TypeTransaction;
import com.example.demo.infra.mapper.TransactionEntityMapper;
import com.example.demo.infra.persistence.entity.CategoryEntity;
import com.example.demo.infra.persistence.entity.TitularEntity;
import com.example.demo.infra.persistence.entity.TransactionEntity;

@Component
public class JpaTransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final JpaTransactionRepository jpaTransactionRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaTitularRepository jpaTitularRepository;
    private final TransactionEntityMapper transactionEntityMapper;

    public JpaTransactionRepositoryAdapter(
        JpaTransactionRepository jpaTransactionRepository,
        JpaCategoryRepository jpaCategoryRepository,
        JpaTitularRepository jpaTitularRepository,
        TransactionEntityMapper transactionEntityMapper
    ) {
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.jpaTitularRepository = jpaTitularRepository;
        this.transactionEntityMapper = transactionEntityMapper;
    }

    @Override
    public List<Transaction> findAll(TransactionListFilter filter) {
        var tipo = filter.tipo().orElse(null);
        var categoriaId = filter.categoriaId().orElse(null);
        LocalDate[] range = resolveDateRange(filter.mes());
        LocalDate desde = range[0];
        LocalDate hasta = range[1];
        return jpaTransactionRepository.findFiltered(tipo, categoriaId, desde, hasta)
            .stream()
            .map(transactionEntityMapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaTransactionRepository.findById(id)
            .map(transactionEntityMapper::toDomain);
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = resolveEntityForSave(transaction);
        attachRelations(entity, transaction);
        TransactionEntity saved = jpaTransactionRepository.save(entity);
        return transactionEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        if (!jpaTransactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transacción no encontrada");
        }
        jpaTransactionRepository.deleteById(id);
    }

    @Override
    public boolean existsByCategoryId(UUID categoryId) {
        return jpaTransactionRepository.existsByCategoryId(categoryId);
    }

    private void attachRelations(TransactionEntity entity, Transaction domain) {
        UUID catId = domain.categoria().categoriaId();
        CategoryEntity category = jpaCategoryRepository.findById(catId)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        TitularEntity titular = jpaTitularRepository.findById(domain.titular().titularId())
            .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado"));
        entity.setCategoria(category);
        entity.setTitular(titular);
    }

    private LocalDate[] resolveDateRange(Optional<YearMonth> monthFilter) {
        return monthFilter
            .map(month -> new LocalDate[] { month.atDay(1), month.atEndOfMonth() })
            .orElseGet(() -> new LocalDate[] { null, null });
    }

    private TransactionEntity resolveEntityForSave(Transaction transaction) {
        return Optional.ofNullable(transaction.transactionId())
            .map(id -> loadEntityForUpdate(id, transaction))
            .orElseGet(() -> createEntityForInsert(transaction));
    }

    private TransactionEntity loadEntityForUpdate(UUID transactionId, Transaction transaction) {
        TransactionEntity entity = jpaTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
        updateEntityFields(entity, transaction);
        return entity;
    }

    private TransactionEntity createEntityForInsert(Transaction transaction) {
        TransactionEntity entity = transactionEntityMapper.toEntity(transaction);
        entity.setFecha(transaction.fecha());
        return entity;
    }

    private void updateEntityFields(TransactionEntity entity, Transaction transaction) {
        entity.setNombre(transaction.nombre());
        entity.setDescripcion(transaction.descripcion());
        entity.setMonto(transaction.monto());
        entity.setTipo(transaction.tipo());
        entity.setFecha(transaction.fecha());
    }

    @Override
    public BigDecimal sumByTitularAndType(UUID titularId, TypeTransaction type) {
        return jpaTransactionRepository.sumByTitularAndType(titularId, type);
    }

    @Override
    public BigDecimal sumByTitularAndTypeAndMonth(UUID titularId, TypeTransaction type, Integer mes,
            Integer anho) {
        return jpaTransactionRepository.sumByTitularAndTypeAndMonth(titularId, type, mes, anho);
    }

}

