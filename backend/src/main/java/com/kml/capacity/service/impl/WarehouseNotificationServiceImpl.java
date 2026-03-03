package com.kml.capacity.service.impl;

import com.kml.capacity.service.WarehouseNotificationService;
import com.kml.domain.notification.ShipmentNotificationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// In Memory Warehouse Notification
@Service
public class WarehouseNotificationServiceImpl implements WarehouseNotificationService {
  private final List<ShipmentNotificationEvent> events = new ArrayList<>();

  @Override
  @Transactional(readOnly = true)
  public void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseIds) {
    for (Long warehouseId : warehouseIds) {
      events.add(new ShipmentNotificationEvent(shipmentId, warehouseId));
    }
  }

  public List<ShipmentNotificationEvent> getEvents() {
    return new ArrayList<>(events);
  }
}
