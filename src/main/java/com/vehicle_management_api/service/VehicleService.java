package com.vehicle_management_api.service;

import com.vehicle_management_api.dto.VehicleDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.entity.Vehicle;
import com.vehicle_management_api.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {

    private static final Logger logger = LogManager.getLogger(VehicleService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @Cacheable(value = "vehicles", key = "#id")
    public Optional<VehicleDTO> findById(Long id) {
        logger.info("Buscando veículo por ID: {}", id);
        return vehicleRepository.findById(id).map(this::convertToDTO);
    }

    @Cacheable(value = "vehicles")
    public List<VehicleDTO> findAll() {
        logger.info("Buscando todos os veículos");
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "vehicles", key = "#brand + '-' + #model")
    public List<VehicleDTO> findByBrandAndModel(String brand, String model) {
        logger.info("Buscando veículos por marca: {} e modelo: {}", brand, model);
        return vehicleRepository.findByBrandAndModel(brand, model).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "vehicles", key = "'available'")
    public List<VehicleDTO> findAvailableVehicles() {
        logger.info("Buscando todos os veículos disponíveis");
        return vehicleRepository.findByIsAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleDTO create(VehicleDTO vehicleDTO, Client createdBy) {
        logger.info("Criando novo veículo com placa: {}", vehicleDTO.getLicensePlate());

        if (vehicleRepository.existsByLicensePlate(vehicleDTO.getLicensePlate())) {
            logger.warn("Veículo com placa {} já existe", vehicleDTO.getLicensePlate());
            throw new RuntimeException("Veículo com esta placa já existe");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        vehicle.setPrice(vehicleDTO.getPrice());
        vehicle.setIsAvailable(vehicleDTO.getIsAvailable() != null ? vehicleDTO.getIsAvailable() : true);
        vehicle.setCreatedBy(createdBy);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        logger.info("Veículo criado com sucesso com ID: {}", savedVehicle.getId());
        return convertToDTO(savedVehicle);
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public Optional<VehicleDTO> update(Long id, VehicleDTO vehicleDTO) {
        logger.info("Atualizando veículo com ID: {}", id);
        return vehicleRepository.findById(id).map(existingVehicle -> {
            existingVehicle.setBrand(vehicleDTO.getBrand());
            existingVehicle.setModel(vehicleDTO.getModel());
            existingVehicle.setYear(vehicleDTO.getYear());
            existingVehicle.setColor(vehicleDTO.getColor());
            existingVehicle.setLicensePlate(vehicleDTO.getLicensePlate());
            existingVehicle.setPrice(vehicleDTO.getPrice());
            if (vehicleDTO.getIsAvailable() != null) {
                existingVehicle.setIsAvailable(vehicleDTO.getIsAvailable());
            }

            Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
            logger.info("Veículo com ID {} atualizado com sucesso", id);
            return convertToDTO(updatedVehicle);
        });
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public boolean delete(Long id) {
        logger.info("Excluindo veículo com ID: {}", id);
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            logger.info("Veículo com ID {} excluído com sucesso", id);
            return true;
        }
        logger.warn("Veículo com ID {} não encontrado para exclusão", id);
        return false;
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setColor(vehicle.getColor());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setPrice(vehicle.getPrice());
        dto.setIsAvailable(vehicle.getIsAvailable());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setCreatedBy(vehicle.getCreatedBy() != null ?
                vehicle.getCreatedBy().getName() : "Sistema");
        return dto;
    }
}



