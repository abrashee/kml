package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.ShipmentResponseDto;

public interface ShipmentService {

  ShipmentResponseDto createShipment(Long orderId, String address, String carrierInfo);

  List<ShipmentResponseDto> getAllShipments();

  ShipmentResponseDto getShipmentById(Long id);

  List<ShipmentResponseDto> getShipmentsByStatus(String status);

  List<ShipmentResponseDto> getShipmentsByOrder(Long orderId);
}
