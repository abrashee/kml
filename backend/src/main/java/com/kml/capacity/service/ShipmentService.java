package com.kml.capacity.service;

import java.util.List;

import com.kml.domain.shipment.Shipment;

public interface ShipmentService {

  Shipment createShipment(Long orderId, String address, String carrierInfo);

  List<Shipment> getAllShipments();

  Shipment getShipmentById(Long id);

  List<Shipment> getShipmentsByStatus(String status);

  List<Shipment> getShipmentsByOrder(Long orderId);
}
