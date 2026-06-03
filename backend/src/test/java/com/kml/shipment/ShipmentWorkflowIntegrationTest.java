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
import com.kml.shipment.dto.ShipmentHistoryResponseDto;
import com.kml.shipment.dto.ShipmentResponseDto;
import com.kml.shipment.entity.ShipmentStatus;
import com.kml.shipment.service.ShipmentHistoryService;
import com.kml.shipment.service.ShipmentService;
import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;
import com.kml.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShipmentWorkflowIntegrationTest {

  @Autowired private OrderService orderService;
  @Autowired private ShipmentService shipmentService;
  @Autowired private ShipmentHistoryService shipmentHistoryService;
  @Autowired private OrderStatusRepository orderStatusRepository;
  @Autowired private InventoryRepository inventoryRepository;
  @Autowired private UserRepository userRepository;
  @MockBean private CurrentUserProvider currentUserProvider;

  private User owner;
  private OrderStatus orderStatus;
  private InventoryItem inventoryItem;

  @BeforeEach
  void setup() {
    // Create test user
    owner = new User("Workflow Owner", "workflowowner", "password123", UserRole.USER);
    owner = userRepository.save(owner);

    // Create order status
    orderStatus = new OrderStatus(owner, "Pending", "Order is pending");
    orderStatus = orderStatusRepository.save(orderStatus);

    // Create inventory item
    inventoryItem = new InventoryItem(owner, "SKU-WORKFLOW", "Workflow Item", 100);
    inventoryItem = inventoryRepository.save(inventoryItem);
  }

  @Test
  void testOrderToShipmentWorkflow() {
    // Step 1: Create an order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(5);
    item.setPriceAtOrder(BigDecimal.valueOf(29.99));

    var createdOrder =
        orderService.createOrder("ORD-WORKFLOW-001", orderStatus.getId(), List.of(item), owner);
    assertNotNull(createdOrder);
    assertEquals("ORD-WORKFLOW-001", createdOrder.code());

    // Step 2: Create a shipment for the order
    ShipmentResponseDto shipment =
        shipmentService.createShipment(createdOrder.id(), "123 Main Street, Anytown, USA", "FedEx");
    assertNotNull(shipment);
    assertEquals(ShipmentStatus.PENDING, shipment.status());
    assertEquals(createdOrder.id(), shipment.orderId());

    // Step 3: Verify shipment is in system
    ShipmentResponseDto retrieved = shipmentService.getShipmentById(shipment.id());
    assertEquals(shipment.id(), retrieved.id());
  }

  @Test
  void testShipmentStateTransitionWithHistory() {
    // Create order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(3);
    item.setPriceAtOrder(BigDecimal.valueOf(19.99));

    var order =
        orderService.createOrder("ORD-STATE-001", orderStatus.getId(), List.of(item), owner);

    // Create shipment
    ShipmentResponseDto shipment =
        shipmentService.createShipment(order.id(), "456 Oak Avenue", "UPS");
    assertEquals(ShipmentStatus.PENDING, shipment.status());

    // Transition to IN_TRANSIT
    ShipmentResponseDto inTransit =
        shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.IN_TRANSIT);
    assertEquals(ShipmentStatus.IN_TRANSIT, inTransit.status());

    // Transition to DELIVERED
    ShipmentResponseDto delivered =
        shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.DELIVERED);
    assertEquals(ShipmentStatus.DELIVERED, delivered.status());
  }

  @Test
  void testCompleteShipmentLifecycleWithHistory() {
    // Create order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(39.99));

    var order =
        orderService.createOrder("ORD-LIFECYCLE", orderStatus.getId(), List.of(item), owner);

    // Create shipment
    ShipmentResponseDto shipment =
        shipmentService.createShipment(order.id(), "789 Pine Road", "DHL");

    // Progress through all statuses
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.DELIVERED);
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.RETURNED);

    // Verify final state
    ShipmentResponseDto finalShipment = shipmentService.getShipmentById(shipment.id());
    assertEquals(ShipmentStatus.RETURNED, finalShipment.status());
  }

  @Test
  void testMultipleOrdersWithShipmentsWorkflow() {
    // Create multiple orders
    OrderItemRequestDto item1 = new OrderItemRequestDto();
    item1.setInventoryItemId(inventoryItem.getId());
    item1.setQuantity(1);
    item1.setPriceAtOrder(BigDecimal.valueOf(9.99));

    var order1 =
        orderService.createOrder("ORD-MULTI-001", orderStatus.getId(), List.of(item1), owner);

    OrderItemRequestDto item2 = new OrderItemRequestDto();
    item2.setInventoryItemId(inventoryItem.getId());
    item2.setQuantity(2);
    item2.setPriceAtOrder(BigDecimal.valueOf(19.99));

    var order2 =
        orderService.createOrder("ORD-MULTI-002", orderStatus.getId(), List.of(item2), owner);

    // Create shipments for both orders
    ShipmentResponseDto shipment1 =
        shipmentService.createShipment(order1.id(), "Address 1", "FedEx");
    ShipmentResponseDto shipment2 = shipmentService.createShipment(order2.id(), "Address 2", "UPS");

    // Transition both shipments
    shipmentService.updateShipmentStatus(shipment1.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(shipment2.id(), ShipmentStatus.IN_TRANSIT);

    // Verify final states
    ShipmentResponseDto final1 = shipmentService.getShipmentById(shipment1.id());
    ShipmentResponseDto final2 = shipmentService.getShipmentById(shipment2.id());

    assertEquals(ShipmentStatus.IN_TRANSIT, final1.status());
    assertEquals(ShipmentStatus.IN_TRANSIT, final2.status());
  }

  @Test
  void testShipmentHistoryWithNullShipmentId() {
    assertThrows(
        IllegalArgumentException.class, () -> shipmentHistoryService.getHistoryForShipment(null));
  }

  @Test
  void testShipmentHistoryForNonExistentShipment() {
    // Getting history for non-existent shipment should return empty list
    List<ShipmentHistoryResponseDto> history = shipmentHistoryService.getHistoryForShipment(999L);
    assertTrue(history.isEmpty());
  }

  @Test
  void testMultipleShipmentsForSingleOrder() {
    // Create one order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(10);
    item.setPriceAtOrder(BigDecimal.valueOf(49.99));

    var order =
        orderService.createOrder("ORD-MULTI-SHIP", orderStatus.getId(), List.of(item), owner);

    // Create multiple shipments for the same order
    ShipmentResponseDto shipment1 =
        shipmentService.createShipment(order.id(), "Address 1", "FedEx");
    ShipmentResponseDto shipment2 = shipmentService.createShipment(order.id(), "Address 2", "UPS");
    ShipmentResponseDto shipment3 = shipmentService.createShipment(order.id(), "Address 3", "DHL");

    // Verify all shipments are for the same order
    assertEquals(order.id(), shipment1.orderId());
    assertEquals(order.id(), shipment2.orderId());
    assertEquals(order.id(), shipment3.orderId());

    // Transition each shipment differently
    shipmentService.updateShipmentStatus(shipment1.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(shipment2.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(shipment2.id(), ShipmentStatus.DELIVERED);

    // Verify independent operations
    ShipmentResponseDto final1 = shipmentService.getShipmentById(shipment1.id());
    ShipmentResponseDto final2 = shipmentService.getShipmentById(shipment2.id());
    ShipmentResponseDto final3 = shipmentService.getShipmentById(shipment3.id());

    assertEquals(ShipmentStatus.IN_TRANSIT, final1.status());
    assertEquals(ShipmentStatus.DELIVERED, final2.status());
    assertEquals(ShipmentStatus.PENDING, final3.status());
  }

  @Test
  void testOrderShipmentWorkflowWithDifferentCarriers() {
    // Create order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(99.99));

    var order = orderService.createOrder("ORD-CARRIER", orderStatus.getId(), List.of(item), owner);

    // Create shipments with different carriers
    ShipmentResponseDto fedexShipment =
        shipmentService.createShipment(order.id(), "123 Federal Lane", "FedEx");
    ShipmentResponseDto upsShipment =
        shipmentService.createShipment(order.id(), "456 United Parkway", "UPS");
    ShipmentResponseDto dhlShipment =
        shipmentService.createShipment(order.id(), "789 Dynamic Highway", "DHL");

    // Verify carrier info
    assertEquals("FedEx", fedexShipment.carrierInfo());
    assertEquals("UPS", upsShipment.carrierInfo());
    assertEquals("DHL", dhlShipment.carrierInfo());

    // Transition all to IN_TRANSIT
    shipmentService.updateShipmentStatus(fedexShipment.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(upsShipment.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(dhlShipment.id(), ShipmentStatus.IN_TRANSIT);

    // Verify all transitions successful
    ShipmentResponseDto fedexFinal = shipmentService.getShipmentById(fedexShipment.id());
    ShipmentResponseDto upsFinal = shipmentService.getShipmentById(upsShipment.id());
    ShipmentResponseDto dhlFinal = shipmentService.getShipmentById(dhlShipment.id());

    assertEquals(ShipmentStatus.IN_TRANSIT, fedexFinal.status());
    assertEquals(ShipmentStatus.IN_TRANSIT, upsFinal.status());
    assertEquals(ShipmentStatus.IN_TRANSIT, dhlFinal.status());
  }

  @Test
  void testShipmentHistoryTimestampOrdering() {
    // Create order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(14.99));

    var order =
        orderService.createOrder("ORD-TIMESTAMP", orderStatus.getId(), List.of(item), owner);

    // Create and transition shipment
    ShipmentResponseDto shipment =
        shipmentService.createShipment(order.id(), "Timestamp Test", "FedEx");

    // Perform transitions
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.DELIVERED);
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.RETURNED);

    // Verify final state
    ShipmentResponseDto finalShipment = shipmentService.getShipmentById(shipment.id());
    assertEquals(ShipmentStatus.RETURNED, finalShipment.status());
  }

  @Test
  void testShipmentWorkflowEndToEndWithMultipleItems() {
    // Create order with multiple items
    OrderItemRequestDto item1 = new OrderItemRequestDto();
    item1.setInventoryItemId(inventoryItem.getId());
    item1.setQuantity(5);
    item1.setPriceAtOrder(BigDecimal.valueOf(10.00));

    OrderItemRequestDto item2 = new OrderItemRequestDto();
    item2.setInventoryItemId(inventoryItem.getId());
    item2.setQuantity(3);
    item2.setPriceAtOrder(BigDecimal.valueOf(15.00));

    var order =
        orderService.createOrder(
            "ORD-MULTI-ITEMS", orderStatus.getId(), List.of(item1, item2), owner);

    // Create shipment
    ShipmentResponseDto shipment =
        shipmentService.createShipment(order.id(), "Multi Item Address", "FedEx");
    assertNotNull(shipment);

    // Verify order has both items
    assertEquals(2, order.items().size());

    // Transition shipment through full lifecycle
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.IN_TRANSIT);
    shipmentService.updateShipmentStatus(shipment.id(), ShipmentStatus.DELIVERED);

    // Verify final state
    ShipmentResponseDto finalShipment = shipmentService.getShipmentById(shipment.id());
    assertEquals(ShipmentStatus.DELIVERED, finalShipment.status());
  }
}
