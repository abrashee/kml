package com.kml.shipment.service;

import java.util.List;

import com.kml.warehouse.entity.Warehouse;

public interface ShipmentWarehouseResolverService {
  List<Warehouse> resolveWarehouseForShipment(Long shipmentId);
}
