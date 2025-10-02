package com.vehicle_management_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String secretKey = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private final Long jwtExpiration = 86400000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);

        userDetails = new User("test@email.com", "password", Collections.emptyList());
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 10); // Token deve ter um tamanho razoável
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("test@email.com", username);
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void shouldNotValidateInvalidToken() {
        // Teste com token inválido - deve lançar exceção que capturamos
        String invalidToken = "invalid.token.here";

        // Verifica se lança exceção (esperado) ou retorna false
        try {
            boolean isValid = jwtService.validateToken(invalidToken, userDetails);
            assertFalse(isValid); // Se não lançar exceção, deve retornar false
        } catch (Exception e) {
            // Exceção é esperada para token inválido
            assertTrue(e instanceof io.jsonwebtoken.MalformedJwtException ||
                    e instanceof IllegalArgumentException);
        }
    }

    @Test
    void shouldNotValidateNullToken() {
        // Teste com token nulo - deve lançar exceção que capturamos
        try {
            boolean isValid = jwtService.validateToken(null, userDetails);
            assertFalse(isValid); // Se não lançar exceção, deve retornar false
        } catch (Exception e) {
            // Exceção é esperada para token nulo
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    void shouldNotValidateEmptyToken() {
        // Teste com token vazio - deve lançar exceção que capturamos
        try {
            boolean isValid = jwtService.validateToken("", userDetails);
            assertFalse(isValid); // Se não lançar exceção, deve retornar false
        } catch (Exception e) {
            // Exceção é esperada para token vazio
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    void shouldExtractClaimFromToken() {
        String token = jwtService.generateToken(userDetails);
        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        assertEquals("test@email.com", subject);
    }
}
