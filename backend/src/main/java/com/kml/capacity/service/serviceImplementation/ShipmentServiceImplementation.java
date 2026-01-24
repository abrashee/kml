package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.service.ShipmentService;
import com.kml.domain.order.Order;
import com.kml.domain.shipment.Shipment;
import com.kml.infra.OrderRepository;
import com.kml.infra.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ShipmentServiceImplementation implements ShipmentService {

  private final ShipmentRepository shipmentRepository;
  private final OrderRepository orderRepository;

  public ShipmentServiceImplementation(
      ShipmentRepository shipmentRepository, OrderRepository orderRepository) {
    this.shipmentRepository = shipmentRepository;
    this.orderRepository = orderRepository;
  }

  @Override
  @Transactional
  public Shipment createShipment(Long orderId, String address, String carrierInfo) {
    Order order =
        this.orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    if (order == null) throw new IllegalArgumentException("Order is required");
    Shipment shipment = Shipment.createWithGeneratedTracking(order, address, carrierInfo);
    return this.shipmentRepository.save(shipment);
  }
}
