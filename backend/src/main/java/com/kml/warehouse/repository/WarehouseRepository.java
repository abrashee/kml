package com.kml.warehouse.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.warehouse.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

  Optional<Warehouse> findByName(String name);

  Page<Warehouse> findAll(Pageable pageable);

  Page<Warehouse> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
