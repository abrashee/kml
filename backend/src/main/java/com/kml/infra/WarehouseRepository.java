package com.kml.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.warehouse.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

  Optional<Warehouse> findByName(String name);
}
