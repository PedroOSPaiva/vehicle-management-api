package com.vehicle_management_api.security;

import com.vehicle_management_api.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Verificar se o header Authorization existe e está no formato correto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        // 2. Validar se o token não está vazio ou malformado
        if (jwt == null || jwt.trim().isEmpty() || !isValidJwtFormat(jwt)) {
            logger.warn("Token JWT malformado ou vazio");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extrair username com tratamento de exceção
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 4. Validar token - versão mais robusta
                if (jwtService.isTokenValid(jwt) && jwtService.validateToken(jwt, userDetails)) {
                    logger.debug("Token JWT válido para usuário: {}", userEmail);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Usuário autenticado via JWT: {}", userEmail);
                } else {
                    logger.warn("Token JWT inválido ou expirado para usuário: {}", userEmail);
                }
            } else if (userEmail == null) {
                logger.warn("Não foi possível extrair username do token JWT");
            }
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Erro ao processar token JWT: {}", e.getMessage());
            // Continua a cadeia de filtros sem autenticar o usuário
        } catch (Exception e) {
            logger.error("Erro inesperado durante autenticação JWT: {}", e.getMessage());
            // Continua a cadeia de filtros sem autenticar o usuário
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Valida o formato básico do JWT (deve conter exatamente 2 pontos)
     */
    private boolean isValidJwtFormat(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // Verifica se tem pelo menos 10 caracteres (mínimo para um JWT válido)
        if (token.length() < 10) {
            return false;
        }

        int dotCount = 0;
        for (char c : token.toCharArray()) {
            if (c == '.') {
                dotCount++;
            }
        }

        return dotCount == 2;
    }
}
