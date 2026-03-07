package com.kml.capacity.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.service.WarehouseNotificationService;
import com.kml.domain.shipment.ShipmentNotificationEvent;

/** In-memory warehouse notification service. Records shipment notifications for warehouses. */
@Service
public class WarehouseNotificationServiceImpl implements WarehouseNotificationService {

  private final List<ShipmentNotificationEvent> events = new ArrayList<>();

  public WarehouseNotificationServiceImpl() {}

  @Override
  @Transactional
  public void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseIds) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");
    if (warehouseIds == null || warehouseIds.isEmpty()) return;

    for (Long warehouseId : warehouseIds) {
      ShipmentNotificationEvent event = new ShipmentNotificationEvent(shipmentId, warehouseId);
      events.add(event);
    }
  }

  public List<ShipmentNotificationEvent> getEvents() {
    return Collections.unmodifiableList(new ArrayList<>(events));
  }
}
