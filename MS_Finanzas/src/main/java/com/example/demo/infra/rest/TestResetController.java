package com.example.demo.infra.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestResetController {

    private final JdbcTemplate jdbc;

    public TestResetController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @DeleteMapping("/reset")
    @Transactional
    public ResponseEntity<Void> reset() {
        jdbc.execute("DELETE FROM transacciones");
        jdbc.execute("DELETE FROM metas_ahorro");
        return ResponseEntity.noContent().build();
    }
}
