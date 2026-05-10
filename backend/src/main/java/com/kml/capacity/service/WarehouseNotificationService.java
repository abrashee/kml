package com.kml.capacity.service;

import java.util.Set;

public interface WarehouseNotificationService {
  void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseId);
}
