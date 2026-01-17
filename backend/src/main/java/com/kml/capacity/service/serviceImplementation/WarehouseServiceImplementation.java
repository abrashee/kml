package com.kml.capacity.service.serviceImplementation;

import org.springframework.stereotype.Service;

import com.kml.capacity.service.WarehouseService;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.WarehouseRepository;

import jakarta.transaction.Transactional;

@Service
public class WarehouseServiceImplementation implements WarehouseService {

  private final WarehouseRepository warehouseRepository;

  public WarehouseServiceImplementation(WarehouseRepository warehouseRepository) {
    this.warehouseRepository = warehouseRepository;
  }

  @Override
  @Transactional
  public Warehouse createWarehouse(String name, String address) {
    Warehouse warehouse = new Warehouse(name, address);
    return warehouseRepository.save(warehouse);
  }
}
