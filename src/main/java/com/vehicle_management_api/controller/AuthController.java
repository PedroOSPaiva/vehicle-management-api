package com.vehicle_management_api.controller;

import com.vehicle_management_api.dto.AuthRequest;
import com.vehicle_management_api.dto.AuthResponse;
import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.service.ClientService;
import com.vehicle_management_api.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "APIs de autenticação")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ClientService clientService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public ResponseEntity<AuthResponse> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("Tentativa de autenticação para usuário: {}", authRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateToken(org.springframework.security.core.userdetails.User
                    .withUsername(authRequest.getEmail())
                    .password("")
                    .authorities(authentication.getAuthorities())
                    .build());

            String refreshToken = jwtService.generateRefreshToken(org.springframework.security.core.userdetails.User
                    .withUsername(authRequest.getEmail())
                    .password("")
                    .authorities(authentication.getAuthorities())
                    .build());

            Client client = clientService.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado"));

            AuthResponse response = new AuthResponse(accessToken, refreshToken,
                    authRequest.getEmail(), client.getUserType().name(), client.getId());

            logger.info("Usuário {} autenticado com sucesso", authRequest.getEmail());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Falha na autenticação para usuário: {}", authRequest.getEmail());
            throw new UsernameNotFoundException("Requisição de usuário inválida!");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar um novo cliente")
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        logger.info("Tentativa de registro para usuário: {}", clientDTO.getEmail());
        ClientDTO createdClient = clientService.create(clientDTO);
        logger.info("Usuário registrado com sucesso com ID: {}", createdClient.getId());
        return ResponseEntity.ok(createdClient);
    }
}