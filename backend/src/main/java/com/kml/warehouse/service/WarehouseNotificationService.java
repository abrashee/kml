package com.kml.warehouse.service;

import java.util.Set;

public interface WarehouseNotificationService {
  void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseId);
}
