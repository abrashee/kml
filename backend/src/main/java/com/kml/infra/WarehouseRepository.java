package com.kml.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.warehouse.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {}
