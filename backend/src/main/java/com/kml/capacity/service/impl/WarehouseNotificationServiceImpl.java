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

  public WarehouseNotificationServiceImpl() {
    // Default constructor
  }

  /**
   * Notify warehouses that a shipment has been created.
   *
   * @param shipmentId the shipment id
   * @param warehouseIds the ids of the warehouses to notify
   */
  @Override
  @Transactional
  public void notifyShipmentCreated(Long shipmentId, Set<Long> warehouseIds) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");
    if (warehouseIds == null || warehouseIds.isEmpty()) return;

    for (Long warehouseId : warehouseIds) {
      // Use the existing constructor that requires shipmentId and warehouseId
      ShipmentNotificationEvent event = new ShipmentNotificationEvent(shipmentId, warehouseId);
      events.add(event);
    }
  }

  /**
   * Get all recorded shipment notification events.
   *
   * @return immutable copy of the events
   */
  public List<ShipmentNotificationEvent> getEvents() {
    return Collections.unmodifiableList(new ArrayList<>(events));
  }
}
