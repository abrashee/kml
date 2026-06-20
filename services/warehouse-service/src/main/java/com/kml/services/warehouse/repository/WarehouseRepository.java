package com.kml.services.warehouse.repository;

import com.kml.services.warehouse.entity.Warehouse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByName(String name);

    List<Warehouse> findByOwnerUserId(Long ownerUserId);
}
