package com.example.demo.infra.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtUtil")
class JwtUtilTest {

    private static final String SECRET =
            "dGVzdFNlY3JldEtleUZvclVuaXRUZXN0c09ubHlOb3RGb3JQcm9kdWN0aW9uVXNlMTIzNDU2Nzg=";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
    }

    private String generateToken(String username, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    private UserDetails userDetails(String username) {
        return User.withUsername(username).password("").authorities("ROLE_USER").build();
    }

    @Test
    @DisplayName("extractUsername retorna el usuario del token")
    void extractUsername_validToken_returnsUsername() {
        String token = generateToken("carlostest", 3_600_000);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("carlostest");
    }

    @Test
    @DisplayName("isTokenValid con token valido y usuario correcto retorna true")
    void isTokenValid_validTokenAndUser_returnsTrue() {
        String token = generateToken("carlostest", 3_600_000);
        assertThat(jwtUtil.isTokenValid(token, userDetails("carlostest"))).isTrue();
    }

    @Test
    @DisplayName("isTokenValid con usuario diferente retorna false")
    void isTokenValid_wrongUser_returnsFalse() {
        String token = generateToken("carlostest", 3_600_000);
        assertThat(jwtUtil.isTokenValid(token, userDetails("otroUsuario"))).isFalse();
    }

    @Test
    @DisplayName("extractUsername con token expirado lanza excepcion")
    void extractUsername_expiredToken_throwsException() {
        String token = generateToken("carlostest", -1_000);
        assertThatThrownBy(() -> jwtUtil.extractUsername(token))
                .isInstanceOf(Exception.class);
    }
}
