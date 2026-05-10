package com.kml.capacity.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.kml.capacity.service.impl.ShipmentServiceImpl;
import com.kml.capacity.service.impl.ShipmentWarehouseResolverServiceImpl;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderStatus;
import com.kml.domain.shipment.Shipment;
import com.kml.domain.shipment.ShipmentStatus;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.OrderRepository;
import com.kml.infra.ShipmentRepository;

public class ShipmentServiceTest {

  @Mock private ShipmentRepository shipmentRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private ShipmentWarehouseResolverServiceImpl shipmentWarehouseResolverService;
  @Mock private WarehouseNotificationService warehouseNotificationService;
  @Mock private SimulationService simulationService;

  @InjectMocks private ShipmentServiceImpl shipmentService;

  private User owner;
  private Order order;
  private Shipment shipment;
  private Warehouse warehouse;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);

    owner = new User("Owner", "owner", "pass", UserRole.USER);
    setId(owner, 1L);

    OrderStatus status = OrderStatus.create(owner, "NEW", "New Order");
    setId(status, 1L);

    order = Order.create(owner, "ORD-001", status);
    setId(order, 1L);

    shipment = Shipment.createWithGeneratedTracking(owner, order, "Address", "Carrier");
    setId(shipment, 1L);

    warehouse = Warehouse.create(owner, "WH", "Address");
    setId(warehouse, 1L);
  }

  private void setId(Object obj, Long id) throws Exception {
    Field field = obj.getClass().getDeclaredField("id");
    field.setAccessible(true);
    field.set(obj, id);
  }

  @Test
  void testCreateShipmentSuccessfully() {
    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
    when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
    when(shipmentWarehouseResolverService.resolveWarehouseForShipment(shipment.getId()))
        .thenReturn(List.of(warehouse));

    var result = shipmentService.createShipment(order.getId(), "Address", "Carrier");

    assertNotNull(result);
    verify(warehouseNotificationService, times(1))
        .notifyShipmentCreated(eq(shipment.getId()), any());
    verify(simulationService, times(1)).startSimulation(shipment.getId());
  }

  @Test
  void testUpdateShipmentStatusSuccessfully() {
    when(shipmentRepository.findById(shipment.getId())).thenReturn(Optional.of(shipment));
    when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);

    var result = shipmentService.updateShipmentStatus(shipment.getId(), ShipmentStatus.IN_TRANSIT);

    // Fixed assertion: compare enums directly
    assertEquals(ShipmentStatus.IN_TRANSIT, result.status());
  }
}
