package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.service.StorageUnitService;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StorageUnitServiceImplementation implements StorageUnitService {
  private final StorageUnitRepository storageUnitRepository;
  private final WarehouseRepository warehouseRepository;

  public StorageUnitServiceImplementation(
      StorageUnitRepository storageUnitRepository, WarehouseRepository warehouseRepository) {
    this.storageUnitRepository = storageUnitRepository;
    this.warehouseRepository = warehouseRepository;
  }

  @Override
  @Transactional
  public StorageUnit createStorageUnit(String code, Long warehouseId, int capacity) {
    Warehouse warehouse =
        this.warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

    StorageUnit storageUnit = new StorageUnit(code, warehouse, capacity);
    return storageUnitRepository.save(storageUnit);
  }
}
