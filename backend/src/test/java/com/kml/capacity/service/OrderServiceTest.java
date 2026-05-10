package com.kml.capacity.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.kml.capacity.dto.order.OrderItemRequestDto;
import com.kml.capacity.dto.order.OrderResponseDto;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.impl.OrderServiceImpl;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderStatus;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.InventoryRepository;
import com.kml.infra.OrderRepository;
import com.kml.infra.OrderStatusRepository;

public class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private OrderStatusRepository orderStatusRepository;

  @Mock private InventoryRepository inventoryRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private OrderServiceImpl orderService;

  private User user;
  private OrderStatus status;
  private InventoryItem inventoryItem;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    user = new User("Test User", "test", "password", UserRole.USER);

    status = OrderStatus.create(user, "NEW", "New Order");

    inventoryItem = InventoryItem.create("SKU123", "Item1", 10, user);
  }

  @Test
  void testCreateOrderSuccessfully() {

    OrderItemRequestDto dto = new OrderItemRequestDto();
    dto.setInventoryItemId(1L);
    dto.setQuantity(2);
    dto.setPriceAtOrder(BigDecimal.valueOf(100));

    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(orderStatusRepository.findById(anyLong())).thenReturn(Optional.of(status));
    when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventoryItem));

    Order savedOrder = Order.create(user, "ORDER001", status);

    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    OrderResponseDto result = orderService.createOrder("ORDER001", 1L, List.of(dto), null);

    assertNotNull(result);
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  void testCreateOrderFailsWhenStatusNotFound() {

    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(orderStatusRepository.findById(anyLong())).thenReturn(Optional.empty());

    OrderItemRequestDto dto = new OrderItemRequestDto();
    dto.setInventoryItemId(1L);
    dto.setQuantity(2);
    dto.setPriceAtOrder(BigDecimal.valueOf(100));

    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> orderService.createOrder("ORDER002", 999L, List.of(dto), null));

    assertTrue(ex.getMessage().contains("Order status not found"));
  }
}
