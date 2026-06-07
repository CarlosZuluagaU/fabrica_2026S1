package com.example.demo.application.repository;

import java.util.Optional;
import java.util.UUID;
import com.example.demo.domain.model.Report;

public interface ReportRepositoryPort {
    Report save(Report report);
    Optional<Report> findByTitularIdAndMesAndAnho(UUID titularId, Integer mes, Integer anho);
}
