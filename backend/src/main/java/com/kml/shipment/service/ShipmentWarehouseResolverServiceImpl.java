package com.kml.shipment.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kml.shipment.entity.Shipment;
import com.kml.shipment.repository.ShipmentRepository;
import com.kml.warehouse.entity.Warehouse;
import com.kml.warehouse.storageUnit.repository.StorageUnitInventoryAssignmentRepository;

@Service
public class ShipmentWarehouseResolverServiceImpl {

  private final ShipmentRepository shipmentRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;

  public ShipmentWarehouseResolverServiceImpl(
      ShipmentRepository shipmentRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository) {
    this.shipmentRepository =
        Objects.requireNonNull(shipmentRepository, "shipmentRepository is required");
    this.assignmentRepository =
        Objects.requireNonNull(assignmentRepository, "assignmentRepository is required");
  }

  public List<Warehouse> resolveWarehouseForShipment(Long shipmentId) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");

    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    return shipment.getOrder().getItems().stream()
        .flatMap(
            orderItem ->
                assignmentRepository
                    .findByInventoryItem_Id(orderItem.getInventoryItem().getId())
                    .stream())
        .map(assignment -> assignment.getStorageUnit().getWarehouse())
        .distinct()
        .collect(Collectors.toList());
  }
}
