package com.vehicle_management_api.service;

import com.vehicle_management_api.dto.VehicleDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.entity.Vehicle;
import com.vehicle_management_api.repository.VehicleRepository;
import com.vehicle_management_api.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleDTO vehicleDTO;
    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("Admin User");
        client.setEmail("admin@test.com");

        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2022);
        vehicle.setColor("Red");
        vehicle.setLicensePlate("ABC1234");
        vehicle.setPrice(new BigDecimal("45000.00"));
        vehicle.setIsAvailable(true);
        vehicle.setCreatedBy(client);

        vehicleDTO = new VehicleDTO();
        vehicleDTO.setBrand("Toyota");
        vehicleDTO.setModel("Corolla");
        vehicleDTO.setYear(2022);
        vehicleDTO.setColor("Red");
        vehicleDTO.setLicensePlate("ABC1234");
        vehicleDTO.setPrice(new BigDecimal("45000.00"));
        vehicleDTO.setIsAvailable(true);
    }

    @Test
    void shouldFindVehicleById() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Optional<VehicleDTO> result = vehicleService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Toyota", result.get().getBrand());
        assertEquals("Corolla", result.get().getModel());
        assertEquals(2022, result.get().getYear());
        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenVehicleNotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<VehicleDTO> result = vehicleService.findById(1L);

        assertFalse(result.isPresent());
        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldFindAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle));

        List<VehicleDTO> result = vehicleService.findAll();

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
        verify(vehicleRepository).findAll();
    }

    @Test
    void shouldCreateVehicle() {
        when(vehicleRepository.existsByLicensePlate("ABC1234")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleDTO result = vehicleService.create(vehicleDTO, client);

        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals(2022, result.getYear());
        assertEquals("Red", result.getColor());
        assertEquals("ABC1234", result.getLicensePlate());
        assertEquals(new BigDecimal("45000.00"), result.getPrice());
        assertTrue(result.getIsAvailable());

        verify(vehicleRepository).existsByLicensePlate("ABC1234");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenLicensePlateExists() {
        when(vehicleRepository.existsByLicensePlate("ABC1234")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vehicleService.create(vehicleDTO, client);
        });

        assertEquals("Veículo com esta placa já existe", exception.getMessage());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void shouldUpdateVehicle() {
        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setBrand("Honda");
        updateDTO.setModel("Civic");
        updateDTO.setYear(2023);
        updateDTO.setColor("Blue");
        updateDTO.setLicensePlate("XYZ5678");
        updateDTO.setPrice(new BigDecimal("50000.00"));
        updateDTO.setIsAvailable(true);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.existsByLicensePlateAndIdNot("XYZ5678", 1L)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Optional<VehicleDTO> result = vehicleService.update(1L, updateDTO);

        assertTrue(result.isPresent());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenLicensePlateExistsOnUpdate() {
        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setLicensePlate("XYZ5678");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.existsByLicensePlateAndIdNot("XYZ5678", 1L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vehicleService.update(1L, updateDTO);
        });

        assertEquals("Veículo com esta placa já existe", exception.getMessage());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void shouldReturnEmptyWhenUpdateNonExistentVehicle() {
        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setBrand("Honda");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<VehicleDTO> result = vehicleService.update(1L, updateDTO);

        assertFalse(result.isPresent());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void shouldDeleteVehicle() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        boolean result = vehicleService.delete(1L);

        assertTrue(result);
        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenDeleteNonExistentVehicle() {
        when(vehicleRepository.existsById(1L)).thenReturn(false);

        boolean result = vehicleService.delete(1L);

        assertFalse(result);
        verify(vehicleRepository, never()).deleteById(1L);
    }

    @Test
    void shouldFindAvailableVehicles() {
        when(vehicleRepository.findByIsAvailableTrue()).thenReturn(Arrays.asList(vehicle));

        List<VehicleDTO> result = vehicleService.findAvailableVehicles();

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
        verify(vehicleRepository).findByIsAvailableTrue();
    }

    @Test
    void shouldFindByBrandAndModel() {
        when(vehicleRepository.findByBrandAndModel("Toyota", "Corolla")).thenReturn(Arrays.asList(vehicle));

        List<VehicleDTO> result = vehicleService.findByBrandAndModel("Toyota", "Corolla");

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
        assertEquals("Corolla", result.get(0).getModel());
        verify(vehicleRepository).findByBrandAndModel("Toyota", "Corolla");
    }
}
