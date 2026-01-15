package com.kml.capacity.service;

import com.kml.domain.inventory.InventoryItem;

public interface InventoryService {

  // Create Inventory Method

  // Inevntory Update Method
  InventoryItem updateQuantity(String sku, int delta);

  // Get All Inventory Method

  // Get Inventory by SKU Method

  // Get Inventory by Id Method

  // Get All Inventories Method

  // Delete Inventory Method
}
