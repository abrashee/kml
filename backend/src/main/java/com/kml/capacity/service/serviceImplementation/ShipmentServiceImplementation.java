package com.kml.capacity.service.serviceImplementation;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

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

@Service
public class ShipmentServiceImplementation implements ShipmentService {

  private final ShipmentRepository shipmentRepository;
  private final OrderRepository orderRepository;
  private final ShipmentWarehouseResolverService shipmentWarehouseResolverService;
  private final WarehouseNotificationService warehouseNotificationService;

  public ShipmentServiceImplementation(
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
  public Shipment createShipment(Long orderId, String address, String carrierInfo) {
    Order order =
        this.orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Shipment shipment = Shipment.createWithGeneratedTracking(order, address, carrierInfo);
    Shipment savedShipment = shipmentRepository.save(shipment);

    try {
      List<Warehouse> warehouses =
          shipmentWarehouseResolverService.resolveWarehouseForShipment(savedShipment.getId());
      warehouses.forEach(
          w ->
              warehouseNotificationService.notifyShipmentCreated(
                  savedShipment.getId(), Set.of(w.getId())));
    } catch (Exception e) {
      System.out.println("Warehouse notification failed");
    }

    return savedShipment;
  }

  @Override
  public List<Shipment> getAllShipments() {
    List<Shipment> shipments = this.shipmentRepository.findAll();
    return shipments;
  }

  @Override
  public Shipment getShipmentById(Long id) {
    Shipment shipment =
        this.shipmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
    return shipment;
  }

  @Override
  public List<Shipment> getShipmentsByStatus(String status) {
    ShipmentStatus shipmentStatus;
    try {
      shipmentStatus = ShipmentStatus.valueOf(status.toUpperCase());

    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid shipment status. Allow values: PENDING, IN_TRANSIT, DELIVERED, RETURNED");
    }
    return this.shipmentRepository.findByStatus(shipmentStatus);
  }

  @Override
  public List<Shipment> getShipmentsByOrder(Long orderId) {
    Order order =
        this.orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    List<Shipment> shipments = this.shipmentRepository.findByOrderId(order.getId());
    return shipments;
  }
}
