package com.kml.shipment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kml.shipment.dto.ShipmentResponseDto;
import com.kml.shipment.entity.ShipmentStatus;

public interface ShipmentService {

  ShipmentResponseDto createShipment(Long orderId, String address, String carrierInfo);

  List<ShipmentResponseDto> getAllShipments();

  ShipmentResponseDto getShipmentById(Long id);

  List<ShipmentResponseDto> getShipmentsByStatus(String status);

  List<ShipmentResponseDto> getShipmentsByOrder(Long orderId);

  ShipmentResponseDto updateShipmentStatus(Long shipmentId, ShipmentStatus nextStatus);

  Page<ShipmentResponseDto> getShipmentsPage(Pageable pageable);
}
