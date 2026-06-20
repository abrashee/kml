package com.kml.services.warehouse.repository;

import com.kml.services.warehouse.entity.StorageUnit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {

    List<StorageUnit> findByWarehouse_Id(Long warehouseId);
}
