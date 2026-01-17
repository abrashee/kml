package com.kml.capacity.service;

import com.kml.domain.warehouse.Warehouse;

public interface WarehouseService {

  // Create
  Warehouse createWarehouse(String name, String address);
}
