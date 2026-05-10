package com.kml.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.kml.capacity.dto.order.OrderItemRequestDto;
import com.kml.capacity.dto.order.OrderResponseDto;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.OrderService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderStatus;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.InventoryRepository;
import com.kml.infra.OrderRepository;
import com.kml.infra.OrderStatusRepository;
import com.kml.infra.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

  @Autowired private OrderService orderService;
  @Autowired private OrderRepository orderRepository;
  @Autowired private OrderStatusRepository orderStatusRepository;
  @Autowired private InventoryRepository inventoryRepository;
  @Autowired private UserRepository userRepository;
  @MockBean private CurrentUserProvider currentUserProvider;

  private User owner;
  private User anotherUser;
  private OrderStatus pendingStatus;
  private OrderStatus shippedStatus;
  private InventoryItem inventoryItem1;
  private InventoryItem inventoryItem2;

  @BeforeEach
  void setup() {
    // Create test users
    owner = new User("Order Owner", "orderowner", "password123", UserRole.USER);
    anotherUser = new User("Another User", "anotheruser", "password456", UserRole.USER);

    owner = userRepository.save(owner);
    anotherUser = userRepository.save(anotherUser);

    // Create order statuses
    pendingStatus = new OrderStatus(owner, "Pending", "Order is pending");
    shippedStatus = new OrderStatus(owner, "Shipped", "Order has been shipped");

    pendingStatus = orderStatusRepository.save(pendingStatus);
    shippedStatus = orderStatusRepository.save(shippedStatus);

    // Create inventory items
    inventoryItem1 = new InventoryItem(owner, "SKU-WIDGET-A", "Widget A", 100);
    inventoryItem2 = new InventoryItem(owner, "SKU-WIDGET-B", "Widget B", 100);

    inventoryItem1 = inventoryRepository.save(inventoryItem1);
    inventoryItem2 = inventoryRepository.save(inventoryItem2);
  }

  @Test
  void testCreateOrderSuccessfully() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-001", pendingStatus.getId(), List.of(item), owner);

    assertNotNull(created);
    assertNotNull(created.id());
    assertEquals("ORD-001", created.code());
    assertEquals(pendingStatus.getId(), created.statusId());
    assertEquals(owner.getId(), created.userId());
    assertEquals(1, created.items().size());
  }

  @Test
  void testCreateOrderWithMultipleItems() {
    OrderItemRequestDto item1 = new OrderItemRequestDto();
    item1.setInventoryItemId(inventoryItem1.getId());
    item1.setQuantity(2);
    item1.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderItemRequestDto item2 = new OrderItemRequestDto();
    item2.setInventoryItemId(inventoryItem2.getId());
    item2.setQuantity(3);
    item2.setPriceAtOrder(BigDecimal.valueOf(14.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-002", pendingStatus.getId(), List.of(item1, item2), owner);

    assertNotNull(created);
    assertEquals(2, created.items().size());
    assertEquals(inventoryItem1.getId(), created.items().get(0).inventoryItemId());
    assertEquals(inventoryItem2.getId(), created.items().get(1).inventoryItemId());
  }

  @Test
  void testCreateOrderWithNullCode() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder(null, pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithBlankCode() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("   ", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithInvalidStatus() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-003", 999L, List.of(item), owner));
  }

  @Test
  void testCreateOrderWithNullItems() {
    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-004", pendingStatus.getId(), null, owner));
  }

  @Test
  void testCreateOrderWithEmptyItems() {
    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-005", pendingStatus.getId(), List.of(), owner));
  }

  @Test
  void testCreateOrderWithInvalidInventoryItem() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(999L);
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-006", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithNegativeQuantity() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(-1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-007", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithZeroQuantity() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(0);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-008", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithNullPrice() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-009", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithNegativePrice() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(-9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-010", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testCreateOrderWithZeroPrice() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.ZERO);

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder("ORD-011", pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testGetOrderByIdSuccessfully() {
    // Create an order first
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-TEST", pendingStatus.getId(), List.of(item), owner);

    // Now retrieve it
    OrderResponseDto retrieved = orderService.getOrderById(created.id());

    assertNotNull(retrieved);
    assertEquals(created.id(), retrieved.id());
    assertEquals("ORD-TEST", retrieved.code());
    assertEquals(pendingStatus.getId(), retrieved.statusId());
  }

  @Test
  void testGetOrderByIdNotFound() {
    assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(999L));
  }

  @Test
  void testGetAllOrders() {
    // Create multiple orders
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    orderService.createOrder("ORD-A", pendingStatus.getId(), List.of(item), owner);
    orderService.createOrder("ORD-B", shippedStatus.getId(), List.of(item), owner);

    List<OrderResponseDto> allOrders = orderService.getAllOrders();

    assertTrue(allOrders.size() >= 2);
  }

  @Test
  void testUpdateOrderSuccessfully() {
    // Create an order first
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-UPDATE", pendingStatus.getId(), List.of(item), owner);

    // Update the order with new status and items
    OrderItemRequestDto newItem = new OrderItemRequestDto();
    newItem.setInventoryItemId(inventoryItem2.getId());
    newItem.setQuantity(3);
    newItem.setPriceAtOrder(BigDecimal.valueOf(14.99));

    OrderResponseDto updated =
        orderService.updateOrder(created.id(), shippedStatus.getId(), List.of(newItem), owner);

    assertNotNull(updated);
    assertEquals(created.id(), updated.id());
    assertEquals("ORD-UPDATE", updated.code());
    assertEquals(1, updated.items().size());
    assertEquals(inventoryItem2.getId(), updated.items().get(0).inventoryItemId());
    assertEquals(3, updated.items().get(0).quantity());
  }

  @Test
  void testUpdateOrderByNonOwner() {
    // Create an order with owner
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-OWNER", pendingStatus.getId(), List.of(item), owner);

    // Try to update with another user
    OrderItemRequestDto newItem = new OrderItemRequestDto();
    newItem.setInventoryItemId(inventoryItem2.getId());
    newItem.setQuantity(1);
    newItem.setPriceAtOrder(BigDecimal.valueOf(14.99));

    assertThrows(
        AccessDeniedException.class,
        () ->
            orderService.updateOrder(
                created.id(), shippedStatus.getId(), List.of(newItem), anotherUser));
  }

  @Test
  void testUpdateNonExistentOrder() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.updateOrder(999L, pendingStatus.getId(), List.of(item), owner));
  }

  @Test
  void testUpdateOrderWithInvalidStatus() {
    // Create an order first
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-STATUS", pendingStatus.getId(), List.of(item), owner);

    // Try to update with invalid status
    OrderItemRequestDto newItem = new OrderItemRequestDto();
    newItem.setInventoryItemId(inventoryItem2.getId());
    newItem.setQuantity(1);
    newItem.setPriceAtOrder(BigDecimal.valueOf(14.99));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.updateOrder(created.id(), 999L, List.of(newItem), owner));
  }

  @Test
  void testDeleteOrderSuccessfully() {
    // Create an order
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-DELETE", pendingStatus.getId(), List.of(item), owner);

    // Delete it
    orderService.deleteOrder(created.id());

    // Verify it's deleted
    assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(created.id()));
  }

  @Test
  void testDeleteNonExistentOrder() {
    assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrder(999L));
  }

  @Test
  void testCreateOrderWithCurrentUserEmpty() {
    // Setup current user as owner
    when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(1);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    // Create order without specifying user (should use currentUserProvider)
    OrderResponseDto created =
        orderService.createOrder("ORD-CURRENT", pendingStatus.getId(), List.of(item), null);

    assertNotNull(created);
    assertEquals(owner.getId(), created.userId());
  }

  @Test
  void testOrderResponseDtoMappingComplete() {
    OrderItemRequestDto item = new OrderItemRequestDto();
    item.setInventoryItemId(inventoryItem1.getId());
    item.setQuantity(2);
    item.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderResponseDto created =
        orderService.createOrder("ORD-MAP", pendingStatus.getId(), List.of(item), owner);

    // Verify all fields are mapped
    assertNotNull(created.id());
    assertEquals("ORD-MAP", created.code());
    assertNotNull(created.statusId());
    assertNotNull(created.statusName());
    assertNotNull(created.userId());
    assertNotNull(created.username());
    assertFalse(created.items().isEmpty());
    assertNotNull(created.createdAt());
    assertNotNull(created.updatedAt());
  }

  @Test
  void testOrderItemsArePersistedInDatabase() {
    OrderItemRequestDto item1 = new OrderItemRequestDto();
    item1.setInventoryItemId(inventoryItem1.getId());
    item1.setQuantity(2);
    item1.setPriceAtOrder(BigDecimal.valueOf(9.99));

    OrderItemRequestDto item2 = new OrderItemRequestDto();
    item2.setInventoryItemId(inventoryItem2.getId());
    item2.setQuantity(3);
    item2.setPriceAtOrder(BigDecimal.valueOf(14.99));

    OrderResponseDto created =
        orderService.createOrder(
            "ORD-PERSIST", pendingStatus.getId(), List.of(item1, item2), owner);

    // Verify in database
    Order retrieved = orderRepository.findById(created.id()).orElseThrow();
    assertEquals(2, retrieved.getItems().size());
    assertEquals(2, retrieved.getItems().get(0).getQuantity());
    assertEquals(3, retrieved.getItems().get(1).getQuantity());
  }
}
