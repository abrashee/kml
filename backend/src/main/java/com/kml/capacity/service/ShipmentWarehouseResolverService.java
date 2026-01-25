package com.kml.capacity.service;

import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.ShipmentWarehouseRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ShipmentWarehouseResolverService {
  private final ShipmentWarehouseRepository shipmentWarehouseRepository;

  public ShipmentWarehouseResolverService(ShipmentWarehouseRepository shipmentWarehouseRepository) {
    this.shipmentWarehouseRepository = shipmentWarehouseRepository;
  }

  public List<Warehouse> resolveWarehouseForShipment(Long shipmentId) {
    return shipmentWarehouseRepository.findWarehouseByShipmentId(shipmentId);
  }
}
