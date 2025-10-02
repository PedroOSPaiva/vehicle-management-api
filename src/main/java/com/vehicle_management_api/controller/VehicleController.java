package com.vehicle_management_api.controller;

import com.vehicle_management_api.dto.VehicleDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.service.ClientService;
import com.vehicle_management_api.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Veículos", description = "APIs de gerenciamento de veículos")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private static final Logger logger = LogManager.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ClientService clientService;

    @GetMapping
    @Operation(summary = "Obter todos os veículos")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        logger.info("Buscando todos os veículos");
        List<VehicleDTO> vehicles = vehicleService.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter veículo por ID")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        logger.info("Buscando veículo por ID: {}", id);
        Optional<VehicleDTO> vehicle = vehicleService.findById(id);
        return vehicle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    @Operation(summary = "Obter veículos disponíveis")
    public ResponseEntity<List<VehicleDTO>> getAvailableVehicles() {
        logger.info("Buscando veículos disponíveis");
        List<VehicleDTO> vehicles = vehicleService.findAvailableVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar veículos por marca e modelo")
    public ResponseEntity<List<VehicleDTO>> searchVehicles(
            @RequestParam String brand,
            @RequestParam String model) {
        logger.info("Buscando veículos por marca: {} e modelo: {}", brand, model);
        List<VehicleDTO> vehicles = vehicleService.findByBrandAndModel(brand, model);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar um novo veículo (Somente administrador)")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        logger.info("Criando novo veículo: {}", vehicleDTO.getLicensePlate());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Client client = clientService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        VehicleDTO createdVehicle = vehicleService.create(vehicleDTO, client);
        logger.info("Veículo criado com sucesso com ID: {}", createdVehicle.getId());
        return ResponseEntity.ok(createdVehicle);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar um veículo (Somente administrador)")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id,
                                                    @Valid @RequestBody VehicleDTO vehicleDTO) {
        logger.info("Atualizando veículo com ID: {}", id);
        Optional<VehicleDTO> updatedVehicle = vehicleService.update(id, vehicleDTO);
        return updatedVehicle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir um veículo (Somente administrador)")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        logger.info("Excluindo veículo com ID: {}", id);
        boolean deleted = vehicleService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
