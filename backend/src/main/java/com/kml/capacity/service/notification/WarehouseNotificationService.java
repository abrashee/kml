package com.kml.capacity.service.notification;

import java.util.Set;

public interface WarehouseNotificationService {
  void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseId);
}
