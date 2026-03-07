package com.kml.capacity.service;

import java.util.List;

import com.kml.domain.warehouse.Warehouse;

public interface ShipmentWarehouseResolverService {
  List<Warehouse> resolveWarehouseForShipment(Long shipmentId);
}
