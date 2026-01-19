package com.kml.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.warehouse.StorageUnit;

public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {

  List<StorageUnit> findByWarehouse_Id(Long warehouseId);

  Optional<StorageUnit> findByCode(String code);

  Optional<StorageUnit> findByWarehouse_IdAndCode(Long warehouseId, String code);
}
