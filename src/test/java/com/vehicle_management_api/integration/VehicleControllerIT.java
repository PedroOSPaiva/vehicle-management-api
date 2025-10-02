package com.vehicle_management_api.integration;

import com.vehicle_management_api.dto.AuthRequest;
import com.vehicle_management_api.dto.AuthResponse;
import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.dto.VehicleDTO;
import com.vehicle_management_api.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VehicleControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken;
    private String uniqueEmail;

    @BeforeEach
    void setUp() {
        // Setup authentication para cada teste
        uniqueEmail = "vehicle_test_" + System.currentTimeMillis() + "@email.com";

        // Registrar usuário
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Vehicle Test User");
        clientDTO.setEmail(uniqueEmail);
        clientDTO.setPassword("password123");
        clientDTO.setUserType(UserType.NORMAL_USER);

        restTemplate.postForEntity("/api/auth/register", clientDTO, ClientDTO.class);

        // Login e obter token
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
    void shouldCreateVehicleWithAuthentication() {
        // Arrange
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setBrand("Honda");
        vehicleDTO.setModel("Civic");
        vehicleDTO.setYear(2023);
        vehicleDTO.setColor("Branco");
        vehicleDTO.setLicensePlate("XYZ" + System.currentTimeMillis());
        vehicleDTO.setPrice(new BigDecimal("110000.00"));
        vehicleDTO.setIsAvailable(true);

        HttpEntity<VehicleDTO> request = new HttpEntity<>(vehicleDTO, createAuthHeaders());

        // Act
        ResponseEntity<VehicleDTO> response = restTemplate
                .postForEntity("/api/vehicles", request, VehicleDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBrand()).isEqualTo("Honda");
        assertThat(response.getBody().getModel()).isEqualTo("Civic");
        assertThat(response.getBody().getLicensePlate()).isEqualTo(vehicleDTO.getLicensePlate());
    }

    @Test
    void shouldGetAllVehiclesWithAuthentication() {
        // Arrange
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());

        // Act
        ResponseEntity<VehicleDTO[]> response = restTemplate
                .exchange("/api/vehicles", HttpMethod.GET, request, VehicleDTO[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnUnauthorizedWithoutToken() {
        // Arrange
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setBrand("Ford");
        vehicleDTO.setModel("Fusion");
        vehicleDTO.setLicensePlate("NOAUTH123");

        HttpEntity<VehicleDTO> request = new HttpEntity<>(vehicleDTO);

        // Act
        ResponseEntity<VehicleDTO> response = restTemplate
                .postForEntity("/api/vehicles", request, VehicleDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldGetAvailableVehicles() {
        // Arrange
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());

        // Act
        ResponseEntity<VehicleDTO[]> response = restTemplate
                .exchange("/api/vehicles/available", HttpMethod.GET, request, VehicleDTO[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldPreventDuplicateLicensePlate() {
        // Arrange
        String licensePlate = "DUP" + System.currentTimeMillis();

        VehicleDTO vehicle1 = new VehicleDTO();
        vehicle1.setBrand("Toyota");
        vehicle1.setModel("Corolla");
        vehicle1.setLicensePlate(licensePlate);
        vehicle1.setYear(2022);
        vehicle1.setPrice(new BigDecimal("90000.00"));

        VehicleDTO vehicle2 = new VehicleDTO();
        vehicle2.setBrand("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setLicensePlate(licensePlate); // Mesma placa
        vehicle2.setYear(2023);
        vehicle2.setPrice(new BigDecimal("95000.00"));

        HttpEntity<VehicleDTO> request1 = new HttpEntity<>(vehicle1, createAuthHeaders());
        HttpEntity<VehicleDTO> request2 = new HttpEntity<>(vehicle2, createAuthHeaders());

        // Primeiro veículo (deve funcionar)
        ResponseEntity<VehicleDTO> response1 = restTemplate
                .postForEntity("/api/vehicles", request1, VehicleDTO.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Segundo veículo com mesma placa (deve falhar)
        ResponseEntity<VehicleDTO> response2 = restTemplate
                .postForEntity("/api/vehicles", request2, VehicleDTO.class);

        // Assert
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
