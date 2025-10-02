package com.vehicle_management_api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle_management_api.dto.VehicleDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.enums.UserType;
import com.vehicle_management_api.service.ClientService;
import com.vehicle_management_api.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private VehicleController vehicleController;

    private ObjectMapper objectMapper;
    private VehicleDTO vehicleDTO;
    private Client client;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
        objectMapper = new ObjectMapper();

        // Setup authentication
        client = new Client();
        client.setId(1L);
        client.setName("Admin User");
        client.setEmail("admin@test.com");
        client.setUserType(UserType.ADMIN);

        Authentication auth = new UsernamePasswordAuthenticationToken("admin@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Setup vehicle
        vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
        vehicleDTO.setBrand("Toyota");
        vehicleDTO.setModel("Corolla");
        vehicleDTO.setYear(2022);
        vehicleDTO.setColor("Red");
        vehicleDTO.setLicensePlate("ABC1234");
        vehicleDTO.setPrice(new BigDecimal("45000.00"));
        vehicleDTO.setIsAvailable(true);
    }

    @Test
    void shouldGetAllVehicles() throws Exception {
        when(vehicleService.findAll()).thenReturn(Arrays.asList(vehicleDTO));

        mockMvc.perform(get("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    @Test
    void shouldGetVehicleById() throws Exception {
        when(vehicleService.findById(1L)).thenReturn(Optional.of(vehicleDTO));

        mockMvc.perform(get("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void shouldReturnNotFoundWhenVehicleNotFound() throws Exception {
        when(vehicleService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAvailableVehicles() throws Exception {
        when(vehicleService.findAvailableVehicles()).thenReturn(Arrays.asList(vehicleDTO));

        mockMvc.perform(get("/vehicles/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    @Test
    void shouldSearchVehiclesByBrandAndModel() throws Exception {
        when(vehicleService.findByBrandAndModel("Toyota", "Corolla")).thenReturn(Arrays.asList(vehicleDTO));

        mockMvc.perform(get("/vehicles/search")
                        .param("brand", "Toyota")
                        .param("model", "Corolla")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    @Test
    void shouldCreateVehicle() throws Exception {
        when(clientService.findByEmail("admin@test.com")).thenReturn(Optional.of(client));
        when(vehicleService.create(any(VehicleDTO.class), any(Client.class))).thenReturn(vehicleDTO);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDTO)))
                .andExpect(status().isOk()) // Seu controller retorna 200 OK, não 201 Created
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void shouldUpdateVehicle() throws Exception {
        when(vehicleService.update(anyLong(), any(VehicleDTO.class))).thenReturn(Optional.of(vehicleDTO));

        mockMvc.perform(put("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateNonExistentVehicle() throws Exception {
        when(vehicleService.update(anyLong(), any(VehicleDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        when(vehicleService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteNonExistentVehicle() throws Exception {
        when(vehicleService.delete(1L)).thenReturn(false);

        mockMvc.perform(delete("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}