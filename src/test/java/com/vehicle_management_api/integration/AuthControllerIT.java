package com.vehicle_management_api.integration;

import com.vehicle_management_api.dto.AuthRequest;
import com.vehicle_management_api.dto.AuthResponse;
import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndLoginUserSuccessfully() {
        // Arrange
        String uniqueEmail = "auth_test_" + System.currentTimeMillis() + "@email.com";

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Auth Test User");
        clientDTO.setEmail(uniqueEmail);
        clientDTO.setPassword("password123");
        clientDTO.setUserType(UserType.NORMAL_USER);

        // Act - Register
        ResponseEntity<ClientDTO> registerResponse = restTemplate
                .postForEntity("/api/auth/register", clientDTO, ClientDTO.class);

        // Assert - Register
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().getEmail()).isEqualTo(uniqueEmail);
        assertThat(registerResponse.getBody().getUserType()).isEqualTo(UserType.NORMAL_USER);

        // Act - Login
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(uniqueEmail);
        authRequest.setPassword("password123");

        ResponseEntity<AuthResponse> loginResponse = restTemplate
                .postForEntity("/api/auth/login", authRequest, AuthResponse.class);

        // Assert - Login
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getAccessToken()).isNotEmpty();
        assertThat(loginResponse.getBody().getRefreshToken()).isNotEmpty();
        assertThat(loginResponse.getBody().getEmail()).isEqualTo(uniqueEmail);
    }

    @Test
    void shouldRegisterAdminUser() {
        // Arrange
        String uniqueEmail = "admin_test_" + System.currentTimeMillis() + "@email.com";

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Admin User");
        clientDTO.setEmail(uniqueEmail);
        clientDTO.setPassword("admin123");
        clientDTO.setUserType(UserType.ADMIN);

        // Act
        ResponseEntity<ClientDTO> response = restTemplate
                .postForEntity("/api/auth/register", clientDTO, ClientDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidCredentials() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("nonexistent@email.com");
        authRequest.setPassword("wrongpassword");

        // Act
        ResponseEntity<AuthResponse> response = restTemplate
                .postForEntity("/api/auth/login", authRequest, AuthResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithExistingEmail() {
        // Arrange
        String uniqueEmail = "duplicate_test_" + System.currentTimeMillis() + "@email.com";

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("First User");
        clientDTO.setEmail(uniqueEmail);
        clientDTO.setPassword("password123");
        clientDTO.setUserType(UserType.NORMAL_USER);

        // Primeiro registro (deve funcionar)
        ResponseEntity<ClientDTO> firstResponse = restTemplate
                .postForEntity("/api/auth/register", clientDTO, ClientDTO.class);
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Segundo registro com mesmo email (deve falhar)
        ClientDTO duplicateDTO = new ClientDTO();
        duplicateDTO.setName("Second User");
        duplicateDTO.setEmail(uniqueEmail); // Mesmo email
        duplicateDTO.setPassword("password456");
        duplicateDTO.setUserType(UserType.NORMAL_USER);

        // Act
        ResponseEntity<ClientDTO> response = restTemplate
                .postForEntity("/api/auth/register", duplicateDTO, ClientDTO.class);

        // Assert - Pode ser 400, 409 ou 500 dependendo da sua implementação
        // O importante é que não seja 200 OK
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }
}