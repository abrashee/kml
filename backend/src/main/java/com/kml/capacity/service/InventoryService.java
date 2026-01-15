package com.kml.capacity.service;

import com.kml.domain.inventory.InventoryItem;
import java.util.List;

public interface InventoryService {

  // Create Inventory Method

  // Inevntory Update Method
  InventoryItem updateQuantity(String sku, int delta);

  // Get All Inventories Method
  List<InventoryItem> getAllInventories();

  // Get Inventory by SKU Method
  InventoryItem getInventoryBySku(String sku);

  // Get Inventory by Id Method
  InventoryItem getInventoryById(Long id);

  // Delete Inventory Method
}
