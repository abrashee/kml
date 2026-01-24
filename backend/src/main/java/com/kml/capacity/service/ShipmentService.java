package com.kml.capacity.service;

import com.kml.domain.shipment.Shipment;

public interface ShipmentService {
  Shipment createShipment(Long orderId, String address, String carrierInfo);
}
