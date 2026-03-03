package com.kml.capacity.service;

import com.kml.domain.order.OrderItem;
import com.kml.domain.shipment.Shipment;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.ShipmentRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ShipmentWarehouseResolverService {

  private final ShipmentRepository shipmentRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;

  public ShipmentWarehouseResolverService(
      ShipmentRepository shipmentRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository) {
    this.shipmentRepository = shipmentRepository;
    this.assignmentRepository = assignmentRepository;
  }

  public List<Warehouse> resolveWarehouseForShipment(Long shipmentId) {

    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    Set<Warehouse> warehouses = new HashSet<>();

    for (OrderItem item : shipment.getOrder().getItems()) {

      List<StorageUnitInventoryAssignment> assignments =
          assignmentRepository.findByInventoryItem_Id(item.getInventoryItem().getId());

      for (StorageUnitInventoryAssignment assignment : assignments) {
        warehouses.add(assignment.getStorageUnit().getWarehouse());
      }
    }

    return List.copyOf(warehouses);
  }
}
