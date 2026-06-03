package com.example.demo.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter")
class JwtAuthFilterTest {

    @Mock private JwtUtil jwtUtil;
    @InjectMocks private JwtAuthFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("sin header Authorization — continúa la cadena sin autenticar")
    void doFilter_noAuthHeader_continuesChain() throws ServletException, IOException {
        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("header sin Bearer — continúa la cadena sin autenticar")
    void doFilter_headerWithoutBearer_continuesChain() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("token valido — establece autenticacion en SecurityContext")
    void doFilter_validToken_setsAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer valid.token.here");
        when(jwtUtil.extractUsername("valid.token.here")).thenReturn("carlostest");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo("carlostest");
    }

    @Test
    @DisplayName("token invalido — continúa la cadena sin autenticar")
    void doFilter_invalidToken_continuesWithoutAuth() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalid.token");
        when(jwtUtil.extractUsername("invalid.token")).thenThrow(new RuntimeException("bad token"));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("usuario ya autenticado — no sobreescribe el SecurityContext")
    void doFilter_alreadyAuthenticated_doesNotOverwrite() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer some.token");
        when(jwtUtil.extractUsername("some.token")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
