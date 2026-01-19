package com.kml.capacity.service;

import com.kml.domain.warehouse.Warehouse;
import java.util.List;
import java.util.Optional;

public interface WarehouseService {

  // Create
  Warehouse createWarehouse(String name, String address);

  // Get
  Optional<Warehouse> getWarehouseById(Long id);

  Optional<Warehouse> getWarehouseByName(String name);

  List<Warehouse> getAllWarehouses();
}
