package com.example.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalDataInitializer implements ApplicationRunner {

    static final String GENERIC_TITULAR_ID = "aa8a8b1d-e583-4168-97f6-64e6a6986397";
    static final String TEST_EMAIL         = "test@finanzas.com";
    static final String TEST_PASSWORD      = "Test1234!";

    private final JdbcTemplate    jdbc;
    private final PasswordEncoder passwordEncoder;

    public LocalDataInitializer(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc            = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer titularCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM titulares_financieros WHERE titular_id = ?",
                Integer.class, GENERIC_TITULAR_ID
            );
            if (titularCount == null || titularCount == 0) {
                jdbc.update(
                    "INSERT INTO titulares_financieros (titular_id, nombre, token, zona_horaria, moneda_preferida, fecha_registro) " +
                    "VALUES (?, 'Test User', '', 'America/Bogota', 'COP', CURRENT_TIMESTAMP)",
                    GENERIC_TITULAR_ID
                );
                System.out.println("[LocalDataInitializer] Titular de prueba creado: " + GENERIC_TITULAR_ID);
            }

            Integer userCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE email = ?",
                Integer.class, TEST_EMAIL
            );
            if (userCount == null || userCount == 0) {
                jdbc.update(
                    "INSERT INTO usuarios (usuario_id, email, password_hash, titular_id) VALUES (RANDOM_UUID(), ?, ?, ?)",
                    TEST_EMAIL, passwordEncoder.encode(TEST_PASSWORD), GENERIC_TITULAR_ID
                );
                System.out.println("[LocalDataInitializer] Usuario de prueba creado: " + TEST_EMAIL);
            }

            // Seed al menos una categoria para que el formulario de transacciones funcione
            Integer catCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM categorias WHERE titular_id = ?",
                Integer.class, GENERIC_TITULAR_ID
            );
            if (catCount == null || catCount == 0) {
                String[] categorias = {"Alimentacion", "Transporte", "Salud", "Entretenimiento", "Ahorro"};
                for (String nombre : categorias) {
                    jdbc.update(
                        "INSERT INTO categorias (categoria_id, nombre, titular_id) VALUES (RANDOM_UUID(), ?, ?)",
                        nombre, GENERIC_TITULAR_ID
                    );
                }
                System.out.println("[LocalDataInitializer] Categorias de prueba creadas.");
            }
        } catch (Exception e) {
            System.err.println("[LocalDataInitializer] Error seeding datos: " + e.getMessage());
        }
    }
}
