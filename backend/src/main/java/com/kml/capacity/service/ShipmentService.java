package com.kml.capacity.service;

import com.kml.domain.shipment.Shipment;
import com.kml.domain.shipment.ShipmentStatus;

public interface ShipmentService {
  Shipment createShipment(Long orderId, String address, String carrierInfo);

  Shipment updateShipmentStatus(Long id, ShipmentStatus nexStatus);
}
