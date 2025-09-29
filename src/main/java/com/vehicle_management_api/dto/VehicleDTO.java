package com.vehicle_management_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;

    @NotBlank(message = "Marca é obrigatória")
    private String brand;

    @NotBlank(message = "Modelo é obrigatório")
    private String model;

    @NotNull(message = "Ano é obrigatório")
    @Positive(message = "Ano deve ser positivo")
    private Integer year;

    private String color;

    @NotBlank(message = "Placa é obrigatória")
    private String licensePlate;

    @Positive(message = "Preço deve ser positivo")
    private BigDecimal price;

    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private String createdBy;
}