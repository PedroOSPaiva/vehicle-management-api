package com.vehicle_management_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand is mandatory")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Model is mandatory")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Year is mandatory")
    @Positive(message = "Year must be positive")
    @Column(nullable = false)
    private Integer year;

    private String color;

    @NotBlank(message = "License plate is mandatory")
    @Column(name = "license_plate", unique = true, nullable = false)
    private String licensePlate;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Client createdBy;
}
