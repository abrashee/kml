package com.kml.capacity.service.notification;

import com.kml.domain.notification.ShipmentNotificationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class InMemoryWarehouseNotificationService implements WarehouseNotificationService {
  private final List<ShipmentNotificationEvent> events = new ArrayList<>();

  @Override
  public void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseIds) {
    for (Long warehouseId : warehouseIds) {
      events.add(new ShipmentNotificationEvent(shipmentId, warehouseId));
    }
  }

  public List<ShipmentNotificationEvent> getEvents() {
    return new ArrayList<>(events);
  }
}
