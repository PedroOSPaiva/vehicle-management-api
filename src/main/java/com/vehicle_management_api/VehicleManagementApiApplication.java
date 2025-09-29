package com.vehicle_management_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VehicleManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleManagementApiApplication.class, args);
	}

}
