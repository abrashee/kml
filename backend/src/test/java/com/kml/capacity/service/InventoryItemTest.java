package com.kml.capacity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;

public class InventoryItemTest {

  private User createTestUser() {
    return new User("Test User", "testuser", "password", UserRole.USER);
  }

  @Test
  void testIncreaseQuantity() {
    User owner = createTestUser();
    InventoryItem item = InventoryItem.create("SKU1", "Item1", 5, owner);

    item.increaseQuantity(10);

    assertEquals(15, item.getQuantity());
  }

  @Test
  void testDecreaseQuantity() {
    User owner = createTestUser();
    InventoryItem item = InventoryItem.create("SKU1", "Item1", 10, owner);

    item.decreaseQuantity(3);

    assertEquals(7, item.getQuantity());
  }

  @Test
  void testDecreaseQuantityFailsWhenNegative() {
    User owner = createTestUser();
    InventoryItem item = InventoryItem.create("SKU1", "Item1", 5, owner);

    Exception ex = assertThrows(IllegalStateException.class, () -> item.decreaseQuantity(6));

    assertTrue(ex.getMessage().contains("Quantity cannot be negative"));
  }
}
