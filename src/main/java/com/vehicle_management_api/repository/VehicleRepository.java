package com.vehicle_management_api.repository;

import com.vehicle_management_api.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByBrand(String brand);

    List<Vehicle> findByBrandAndModel(String brand, String model);

    List<Vehicle> findByIsAvailableTrue();

    @Query("SELECT v FROM Vehicle v WHERE v.brand LIKE %:brand% OR v.model LIKE %:model%")
    List<Vehicle> findByBrandOrModelContaining(@Param("brand") String brand, @Param("model") String model);

    Boolean existsByLicensePlate(String licensePlate);
}
