package com.vehicle_management_api.repository;

import com.vehicle_management_api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT c FROM Client c WHERE c.email = :email AND c.isActive = true")
    Optional<Client> findByEmailAndActive(@Param("email") String email);
}
