package com.kml.shipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kml.inventory.entity.InventoryItem;
import com.kml.inventory.repository.InventoryRepository;
import com.kml.order.dto.OrderItemRequestDto;
import com.kml.order.entity.OrderStatus;
import com.kml.order.repository.OrderStatusRepository;
import com.kml.order.service.OrderService;
import com.kml.security.CurrentUserProvider;
import com.kml.shipment.dto.ShipmentResponseDto;
import com.kml.shipment.entity.Shipment;
import com.kml.shipment.entity.ShipmentStatus;
import com.kml.shipment.repository.ShipmentRepository;
import com.kml.shipment.service.ShipmentService;
import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;
import com.kml.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShipmentServiceIntegrationTest {

  @Autowired private ShipmentService shipmentService;
  @Autowired private OrderService orderService;
  @Autowired private ShipmentRepository shipmentRepository;
  @Autowired private OrderStatusRepository orderStatusRepository;
  @Autowired private InventoryRepository inventoryRepository;
  @Autowired private UserRepository userRepository;
  @MockBean private CurrentUserProvider currentUserProvider;

  private User owner;
  private OrderStatus pendingStatus;
  private InventoryItem inventoryItem;
  private Long orderId;

  @BeforeEach
  void setup() {
    // Create test user
    owner = new User("Shipment Owner", "shipowner", "password123", UserRole.USER);
    owner = userRepository.save(owner);

    // Create order status
    pendingStatus = new OrderStatus(owner, "Pending", "Order is pending");
    pendingStatus = orderStatusRepository.save(pendingStatus);

    // Create inventory item
    inventoryItem = new InventoryItem(owner, "SKU-SHIP-001", "Shippable Item", 100);
    inventoryItem = inventoryRepository.save(inventoryItem);

    // Create an order to use for shipments
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(19.99));

    var createdOrder =
        orderService.createOrder("ORD-SHIP", pendingStatus.getId(), List.of(item), owner);
    orderId = createdOrder.id();
  }

  @Test
  void testCreateShipmentSuccessfully() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "123 Delivery Street", "FedEx");

    assertNotNull(created);
    assertNotNull(created.id());
    assertNotNull(created.tracking());
    assertEquals("123 Delivery Street", created.address());
    assertEquals("FedEx", created.carrierInfo());
    assertEquals(ShipmentStatus.PENDING, created.status());
    assertEquals(orderId, created.orderId());
  }

  @Test
  void testCreateShipmentWithNullOrderId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.createShipment(null, "123 Delivery Street", "FedEx"));
  }

  @Test
  void testCreateShipmentWithInvalidOrderId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.createShipment(999L, "123 Delivery Street", "FedEx"));
  }

  @Test
  void testCreateShipmentWithNullAddress() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.createShipment(orderId, null, "FedEx"));
  }

  @Test
  void testCreateShipmentWithBlankAddress() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.createShipment(orderId, "   ", "FedEx"));
  }

  @Test
  void testCreateShipmentWithoutCarrierInfo() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "456 Shipping Lane", null);

    assertNotNull(created);
    assertEquals("456 Shipping Lane", created.address());
  }

  @Test
  void testGetShipmentByIdSuccessfully() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "789 Route Street", "UPS");

    ShipmentResponseDto retrieved = shipmentService.getShipmentById(created.id());

    assertNotNull(retrieved);
    assertEquals(created.id(), retrieved.id());
    assertEquals("789 Route Street", retrieved.address());
  }

  @Test
  void testGetShipmentByIdNotFound() {
    assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipmentById(999L));
  }

  @Test
  void testGetShipmentByIdWithNullId() {
    assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipmentById(null));
  }

  @Test
  void testGetAllShipments() {
    shipmentService.createShipment(orderId, "Address 1", "Carrier 1");
    shipmentService.createShipment(orderId, "Address 2", "Carrier 2");

    List<ShipmentResponseDto> allShipments = shipmentService.getAllShipments();

    assertTrue(allShipments.size() >= 2);
  }

  @Test
  void testGetShipmentsByStatusPending() {
    shipmentService.createShipment(orderId, "Pending Address", "Carrier A");

    List<ShipmentResponseDto> pendingShipments = shipmentService.getShipmentsByStatus("PENDING");

    assertTrue(pendingShipments.size() >= 1);
    assertTrue(pendingShipments.stream().allMatch(s -> s.status() == ShipmentStatus.PENDING));
  }

  @Test
  void testGetShipmentsByStatusInTransit() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "Transit Address", "Carrier B");

    // Transition to IN_TRANSIT
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.IN_TRANSIT);

    List<ShipmentResponseDto> inTransitShipments =
        shipmentService.getShipmentsByStatus("IN_TRANSIT");

    assertTrue(inTransitShipments.size() >= 1);
    assertTrue(inTransitShipments.stream().allMatch(s -> s.status() == ShipmentStatus.IN_TRANSIT));
  }

  @Test
  void testGetShipmentsByStatusInvalidStatus() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.getShipmentsByStatus("INVALID_STATUS"));
  }

  @Test
  void testGetShipmentsByStatusNullStatus() {
    assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipmentsByStatus(null));
  }

  @Test
  void testGetShipmentsByStatusBlankStatus() {
    assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipmentsByStatus("   "));
  }

  @Test
  void testGetShipmentsByOrder() {
    ShipmentResponseDto shipment1 =
        shipmentService.createShipment(orderId, "Address A", "Carrier A");
    ShipmentResponseDto shipment2 =
        shipmentService.createShipment(orderId, "Address B", "Carrier B");

    List<ShipmentResponseDto> orderShipments = shipmentService.getShipmentsByOrder(orderId);

    assertTrue(orderShipments.size() >= 2);
    assertTrue(orderShipments.stream().allMatch(s -> s.orderId().equals(orderId)));
  }

  @Test
  void testGetShipmentsByOrderNotFound() {
    assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipmentsByOrder(999L));
  }

  @Test
  void testGetShipmentsByOrderNullOrderId() {
    assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipmentsByOrder(null));
  }

  @Test
  void testUpdateShipmentStatusPendingToInTransit() {
    ShipmentResponseDto created = shipmentService.createShipment(orderId, "Transit Test", "FedEx");

    ShipmentResponseDto updated =
        shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.IN_TRANSIT);

    assertNotNull(updated);
    assertEquals(ShipmentStatus.IN_TRANSIT, updated.status());
  }

  @Test
  void testUpdateShipmentStatusInTransitToDelivered() {
    ShipmentResponseDto created = shipmentService.createShipment(orderId, "Delivery Test", "UPS");

    // First transition to IN_TRANSIT
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.IN_TRANSIT);

    // Then transition to DELIVERED
    ShipmentResponseDto updated =
        shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.DELIVERED);

    assertEquals(ShipmentStatus.DELIVERED, updated.status());
  }

  @Test
  void testUpdateShipmentStatusDeliveredToReturned() {
    ShipmentResponseDto created = shipmentService.createShipment(orderId, "Return Test", "FedEx");

    // Transition through all valid statuses
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.DELIVERED);

    // Then transition to RETURNED
    ShipmentResponseDto updated =
        shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.RETURNED);

    assertEquals(ShipmentStatus.RETURNED, updated.status());
  }

  @Test
  void testUpdateShipmentStatusInvalidTransitionPendingToDelivered() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "Invalid Transition", "FedEx");

    assertThrows(
        IllegalStateException.class,
        () -> shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.DELIVERED));
  }

  @Test
  void testUpdateShipmentStatusInvalidTransitionReturnedToAny() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "Final Status Test", "UPS");

    // Transition to RETURNED (final state)
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.DELIVERED);
    shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.RETURNED);

    // Try to transition from RETURNED
    assertThrows(
        IllegalStateException.class,
        () -> shipmentService.updateShipmentStatus(created.id(), ShipmentStatus.IN_TRANSIT));
  }

  @Test
  void testUpdateShipmentStatusWithNullShipmentId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.updateShipmentStatus(null, ShipmentStatus.IN_TRANSIT));
  }

  @Test
  void testUpdateShipmentStatusWithNullStatus() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "Null Status Test", "FedEx");

    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.updateShipmentStatus(created.id(), null));
  }

  @Test
  void testUpdateShipmentStatusNonExistentShipment() {
    assertThrows(
        IllegalArgumentException.class,
        () -> shipmentService.updateShipmentStatus(999L, ShipmentStatus.IN_TRANSIT));
  }

  @Test
  void testShipmentPersistenceInDatabase() {
    ShipmentResponseDto created =
        shipmentService.createShipment(orderId, "Persistence Test", "DHL");

    // Verify in database
    Shipment retrieved = shipmentRepository.findById(created.id()).orElseThrow();
    assertEquals("Persistence Test", retrieved.getAddress());
    assertEquals("DHL", retrieved.getCarrierInfo());
    assertNotNull(retrieved.getTracking());
    assertEquals(ShipmentStatus.PENDING, retrieved.getStatus());
    assertEquals(orderId, retrieved.getOrder().getId());
  }

  @Test
  void testShipmentDtoMappingComplete() {
    ShipmentResponseDto created = shipmentService.createShipment(orderId, "Mapping Test", "FedEx");

    // Verify all fields are mapped
    assertNotNull(created.id());
    assertNotNull(created.tracking());
    assertEquals("Mapping Test", created.address());
    assertEquals("FedEx", created.carrierInfo());
    assertNotNull(created.status());
    assertNotNull(created.createdAt());
    assertNotNull(created.updatedAt());
    assertEquals(orderId, created.orderId());
  }

  @Test
  void testShipmentFullLifecycle() {
    // Create shipment
    ShipmentResponseDto shipment = shipmentService.createShipment(orderId, "Lifecycle Test", "UPS");
    assertEquals(ShipmentStatus.PENDING, shipment.status());

    // Transition to IN_TRANSIT
    shipment = shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.IN_TRANSIT);
    assertEquals(ShipmentStatus.IN_TRANSIT, shipment.status());

    // Transition to DELIVERED
    shipment = shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.DELIVERED);
    assertEquals(ShipmentStatus.DELIVERED, shipment.status());

    // Transition to RETURNED
    shipment = shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.RETURNED);
    assertEquals(ShipmentStatus.RETURNED, shipment.status());

    // Verify final state in database
    Shipment finalShipment = shipmentRepository.findById(shipment.id()).orElseThrow();
    assertEquals(ShipmentStatus.RETURNED, finalShipment.getStatus());
  }

  @Test
  void testMultipleShipmentsPerOrder() {
    ShipmentResponseDto shipment1 =
        shipmentService.createShipment(orderId, "First Delivery", "FedEx");
    ShipmentResponseDto shipment2 =
        shipmentService.createShipment(orderId, "Second Delivery", "UPS");
    ShipmentResponseDto shipment3 =
        shipmentService.createShipment(orderId, "Third Delivery", "DHL");

    List<ShipmentResponseDto> orderShipments = shipmentService.getShipmentsByOrder(orderId);

    assertEquals(3, orderShipments.size());
    assertTrue(orderShipments.stream().anyMatch(s -> s.id().equals(shipment1.id())));
    assertTrue(orderShipments.stream().anyMatch(s -> s.id().equals(shipment2.id())));
    assertTrue(orderShipments.stream().anyMatch(s -> s.id().equals(shipment3.id())));
  }
}
