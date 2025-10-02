package com.vehicle_management_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle_management_api.dto.AuthRequest;
import com.vehicle_management_api.dto.AuthResponse;
import com.vehicle_management_api.dto.ClientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        authRequest = new AuthRequest();
        authRequest.setEmail("test@email.com");
        authRequest.setPassword("password");

        authResponse = new AuthResponse();
        authResponse.setToken("jwt-token-here");
        authResponse.setEmail("test@email.com");

        clientDTO = new ClientDTO();
        clientDTO.setName("Test User");
        clientDTO.setEmail("test@email.com");
        clientDTO.setPassword("password");
        clientDTO.setUserType(UserType.NORMAL_USER);
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        when(authService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void shouldRegisterUser() throws Exception {
        when(authService.register(any(ClientDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void shouldRefreshToken() throws Exception {
        when(authService.refreshToken(any(String.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"refresh-token-here\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void shouldLogoutUser() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"refresh-token-here\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"));
    }
}
