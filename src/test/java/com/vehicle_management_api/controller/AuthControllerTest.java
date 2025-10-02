package com.vehicle_management_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle_management_api.dto.AuthRequest;
import com.vehicle_management_api.dto.AuthResponse;
import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.enums.UserType;
import com.vehicle_management_api.service.ClientService;
import com.vehicle_management_api.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private AuthRequest authRequest;
    private ClientDTO clientDTO;
    private Client client;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        authRequest = new AuthRequest();
        authRequest.setEmail("test@email.com");
        authRequest.setPassword("password");

        clientDTO = new ClientDTO();
        clientDTO.setName("Test User");
        clientDTO.setEmail("test@email.com");
        clientDTO.setPassword("password");
        clientDTO.setUserType(UserType.NORMAL_USER);

        client = new Client();
        client.setId(1L);
        client.setName("Test User");
        client.setEmail("test@email.com");
        client.setUserType(UserType.NORMAL_USER);
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        // Mock authentication com authorities E authenticated = true
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + UserType.NORMAL_USER.name())
        );

        // Crie um mock do Authentication em vez de uma instância real
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword(),
                authorities) {
            @Override
            public boolean isAuthenticated() {
                return true; // SEMPRE retorna true
            }
        };

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(any())).thenReturn("access-token-here");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token-here");
        when(clientService.findByEmail("test@email.com")).thenReturn(Optional.of(client));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-here"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-here"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void shouldRegisterUser() throws Exception {
        when(clientService.create(any(ClientDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }
}