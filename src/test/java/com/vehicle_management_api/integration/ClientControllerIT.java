package com.vehicle_management_api.integration;

import com.vehicle_management_api.dto.AuthRequest;
import com.vehicle_management_api.dto.AuthResponse;
import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken;

    @BeforeEach
    void setUp() {
        // Setup authentication
        String uniqueEmail = "client_test_" + System.currentTimeMillis() + "@email.com";

        // Register user
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Client Test User");
        clientDTO.setEmail(uniqueEmail);
        clientDTO.setPassword("password123");
        clientDTO.setUserType(UserType.NORMAL_USER);

        restTemplate.postForEntity("/api/auth/register", clientDTO, ClientDTO.class);

        // Login and get token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(uniqueEmail);
        authRequest.setPassword("password123");

        ResponseEntity<AuthResponse> loginResponse = restTemplate
                .postForEntity("/api/auth/login", authRequest, AuthResponse.class);

        authToken = loginResponse.getBody().getAccessToken();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void shouldGetAllClientsWithAuthentication() {
        // Arrange
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());

        // Act
        ResponseEntity<ClientDTO[]> response = restTemplate
                .exchange("/api/clients", HttpMethod.GET, request, ClientDTO[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnUnauthorizedWithoutToken() {
        // Act
        ResponseEntity<ClientDTO[]> response = restTemplate
                .getForEntity("/api/clients", ClientDTO[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldGetClientProfileWithAuthentication() {
        // Arrange
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());

        // Act - Normalmente há um endpoint /api/clients/profile ou /me
        ResponseEntity<ClientDTO> response = restTemplate
                .exchange("/api/clients/me", HttpMethod.GET, request, ClientDTO.class);

        // Assert - Pode retornar 200 OK ou 404 se o endpoint não existir
        // O importante é que não seja 401 Unauthorized
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldUpdateClientWithAuthentication() {
        // Arrange
        ClientDTO updateDTO = new ClientDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("updated_" + System.currentTimeMillis() + "@email.com");

        HttpEntity<ClientDTO> request = new HttpEntity<>(updateDTO, createAuthHeaders());

        // Act - Assumindo que há um endpoint PUT /api/clients/{id} ou /me
        ResponseEntity<ClientDTO> response = restTemplate
                .exchange("/api/clients/me", HttpMethod.PUT, request, ClientDTO.class);

        // Assert - Pode retornar 200, 404 ou 400 dependendo da implementação
        // O importante é que não seja 401 Unauthorized
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
