package com.kml.capacity.service.impl;

import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.capacity.mapper.ShipmentMapper;
import com.kml.capacity.service.ShipmentService;
import com.kml.capacity.service.ShipmentWarehouseResolverService;
import com.kml.capacity.service.notification.WarehouseNotificationService;
import com.kml.domain.order.Order;
import com.kml.domain.shipment.Shipment;
import com.kml.domain.shipment.ShipmentStatus;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.OrderRepository;
import com.kml.infra.ShipmentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShipmentServiceImpl implements ShipmentService {

  private final ShipmentRepository shipmentRepository;
  private final OrderRepository orderRepository;
  private final ShipmentWarehouseResolverService shipmentWarehouseResolverService;
  private final WarehouseNotificationService warehouseNotificationService;
  private static final Logger log = LoggerFactory.getLogger(ShipmentServiceImpl.class);

  public ShipmentServiceImpl(
      ShipmentRepository shipmentRepository,
      OrderRepository orderRepository,
      ShipmentWarehouseResolverService shipmentWarehouseResolverService,
      WarehouseNotificationService warehouseNotificationService) {
    this.shipmentRepository = shipmentRepository;
    this.orderRepository = orderRepository;
    this.shipmentWarehouseResolverService = shipmentWarehouseResolverService;
    this.warehouseNotificationService = warehouseNotificationService;
  }

  @Override
  @Transactional
  public ShipmentResponseDto createShipment(Long orderId, String address, String carrierInfo) {
    Order order =
        this.orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Shipment shipment = Shipment.createWithGeneratedTracking(order, address, carrierInfo);
    Shipment savedShipment = shipmentRepository.save(shipment);

    List<Warehouse> warehouses = List.of();

    try {
      warehouses =
          shipmentWarehouseResolverService.resolveWarehouseForShipment(savedShipment.getId());

      warehouses.forEach(
          w ->
              warehouseNotificationService.notifyShipmentCreated(
                  savedShipment.getId(), Set.of(w.getId())));
    } catch (Exception e) {
      log.error(
          "Failed to notify warehouses for shipment id={}, orderId={}, warehouseCount={}",
          savedShipment.getId(),
          orderId,
          warehouses.size(),
          e);
    }

    return ShipmentMapper.toDto(savedShipment);
  }

  @Override
  public List<ShipmentResponseDto> getAllShipments() {
    return this.shipmentRepository.findAll().stream().map(ShipmentMapper::toDto).toList();
  }

  @Override
  public ShipmentResponseDto getShipmentById(Long id) {
    Shipment shipment =
        this.shipmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
    return ShipmentMapper.toDto(shipment);
  }

  @Override
  public List<ShipmentResponseDto> getShipmentsByStatus(String status) {
    ShipmentStatus shipmentStatus;
    try {
      shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid shipment status. Allow values: PENDING, IN_TRANSIT, DELIVERED, RETURNED");
    }

    return this.shipmentRepository.findByStatus(shipmentStatus).stream()
        .map(ShipmentMapper::toDto)
        .toList();
  }

  @Override
  public List<ShipmentResponseDto> getShipmentsByOrder(Long orderId) {
    Order order =
        this.orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    return this.shipmentRepository.findByOrderId(order.getId()).stream()
        .map(ShipmentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ShipmentResponseDto updateShipmentStatus(Long shipmentId, ShipmentStatus nextStatus) {
    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    shipment.transitionTo(nextStatus);

    Shipment saved = shipmentRepository.save(shipment);
    return ShipmentMapper.toDto(saved);
  }
}
