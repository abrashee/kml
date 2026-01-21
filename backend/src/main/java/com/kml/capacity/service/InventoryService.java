package com.kml.capacity.service;

import java.util.List;

import com.kml.domain.inventory.InventoryItem;

public interface InventoryService {

  // Create Inventory

  // Inevntory Update (increase or decrease)
  InventoryItem updateQuantity(String sku, int delta);

  // Get All Inventories
  List<InventoryItem> getAllInventories();

  // Get Inventory by SKU
  InventoryItem getInventoryBySku(String sku);

  // Get Inventory by Id
  InventoryItem getInventoryById(Long id);

  // Search Inventory by Name
  List<InventoryItem> getInventoryByName(String name);

  // Filter Inventory by Quantity Range
  List<InventoryItem> getInventoryByRange(int minQuantity, int maxQuantity);

  // | Combined Filters (SKU + name)
  List<InventoryItem> getInventoryByFilter(String sku, String name);

  List<InventoryItem> getInventoryByStorageUnitId(Long id);

  List<InventoryItem> getInventoryByWarehouseId(Long id);
}
