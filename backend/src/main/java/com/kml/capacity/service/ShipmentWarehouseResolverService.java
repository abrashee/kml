package com.kml.capacity.service;

import com.kml.domain.shipment.Shipment;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.ShipmentRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import java.util.List;
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
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");

    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    return shipment.getOrder().getItems().stream()
        .flatMap(
            item ->
                assignmentRepository
                    .findByInventoryItem_Id(item.getInventoryItem().getId())
                    .stream())
        .map(a -> a.getStorageUnit().getWarehouse())
        .distinct()
        .toList();
  }
}
