package com.kml.shipment.service;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.order.entity.Order;
import com.kml.order.repository.OrderRepository;
import com.kml.shipment.dto.ShipmentResponseDto;
import com.kml.shipment.entity.Shipment;
import com.kml.shipment.entity.ShipmentStatus;
import com.kml.shipment.mapper.ShipmentMapper;
import com.kml.shipment.repository.ShipmentRepository;
import com.kml.warehouse.entity.Warehouse;
import com.kml.warehouse.service.WarehouseNotificationService;

@Service
public class ShipmentServiceImpl implements ShipmentService {

  private static final Logger log = LoggerFactory.getLogger(ShipmentServiceImpl.class);

  private final ShipmentRepository shipmentRepository;
  private final OrderRepository orderRepository;
  private final ShipmentWarehouseResolverServiceImpl shipmentWarehouseResolverService;
  private final WarehouseNotificationService warehouseNotificationService;
  private final ShipmentSimulationService simulationService;

  public ShipmentServiceImpl(
      ShipmentRepository shipmentRepository,
      OrderRepository orderRepository,
      ShipmentWarehouseResolverServiceImpl shipmentWarehouseResolverService,
      WarehouseNotificationService warehouseNotificationService,
      ShipmentSimulationService simulationService) {

    this.shipmentRepository = shipmentRepository;
    this.orderRepository = orderRepository;
    this.shipmentWarehouseResolverService = shipmentWarehouseResolverService;
    this.warehouseNotificationService = warehouseNotificationService;
    this.simulationService = simulationService;
  }

  @Override
  @Transactional
  public ShipmentResponseDto createShipment(Long orderId, String address, String carrierInfo) {

    if (orderId == null) throw new IllegalArgumentException("OrderId is required");
    if (address == null || address.isBlank())
      throw new IllegalArgumentException("Address is required");

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Shipment shipment =
        Shipment.createWithGeneratedTracking(order.getOwner(), order, address, carrierInfo);
    Shipment savedShipment = shipmentRepository.save(shipment);

    try {
      List<Warehouse> warehouses =
          shipmentWarehouseResolverService.resolveWarehouseForShipment(savedShipment.getId());
      for (Warehouse w : warehouses) {
        warehouseNotificationService.notifyShipmentCreated(
            savedShipment.getId(), Set.of(w.getId()));
      }
    } catch (Exception e) {
      log.error("Failed to notify warehouses for shipment id={}", savedShipment.getId(), e);
    }

    simulationService.startSimulation(savedShipment.getId());

    return ShipmentMapper.toDto(savedShipment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentResponseDto> getAllShipments() {
    return shipmentRepository.findAll().stream().map(ShipmentMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ShipmentResponseDto getShipmentById(Long id) {
    if (id == null) throw new IllegalArgumentException("ShipmentId is required");
    Shipment shipment =
        shipmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
    return ShipmentMapper.toDto(shipment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentResponseDto> getShipmentsByStatus(String status) {
    if (status == null || status.isBlank())
      throw new IllegalArgumentException("Status is required");

    ShipmentStatus shipmentStatus;
    try {
      shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid shipment status. Allowed values: PENDING, IN_TRANSIT, DELIVERED, RETURNED");
    }

    return shipmentRepository.findByStatus(shipmentStatus).stream()
        .map(ShipmentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentResponseDto> getShipmentsByOrder(Long orderId) {
    if (orderId == null) throw new IllegalArgumentException("OrderId is required");

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    return shipmentRepository.findByOrderId(order.getId()).stream()
        .map(ShipmentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ShipmentResponseDto updateShipmentStatus(Long shipmentId, ShipmentStatus nextStatus) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");
    if (nextStatus == null) throw new IllegalArgumentException("NextStatus is required");

    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    shipment.transitionTo(nextStatus);
    Shipment savedShipment = shipmentRepository.save(shipment);

    return ShipmentMapper.toDto(savedShipment);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ShipmentResponseDto> getShipmentsPage(Pageable pageable) {
    return shipmentRepository.findAll(pageable).map(ShipmentMapper::toDto);
  }
}
