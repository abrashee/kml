package com.kml.capacity.service;

import java.util.List;

import com.kml.domain.warehouse.Warehouse;

public interface ShipmentWarehouseResolverService {

  /**
   * Resolves the warehouses responsible for fulfilling a shipment by looking at inventory
   * assignments of each order item.
   *
   * @param shipmentId the shipment id to resolve
   * @return distinct list of warehouses involved in the shipment
   */
  List<Warehouse> resolveWarehouseForShipment(Long shipmentId);
}
