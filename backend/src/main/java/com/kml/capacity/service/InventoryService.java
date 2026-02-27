package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.InventoryItemResponseDto;

public interface InventoryService {

  InventoryItemResponseDto createInventoryItem(String sku, String name, int quantity);

  InventoryItemResponseDto updateQuantity(String sku, int delta);

  List<InventoryItemResponseDto> getAllInventories();

  InventoryItemResponseDto getInventoryBySku(String sku);

  InventoryItemResponseDto getInventoryById(Long id);

  List<InventoryItemResponseDto> getInventoryByName(String name);

  List<InventoryItemResponseDto> getInventoryByRange(int minQuantity, int maxQuantity);

  List<InventoryItemResponseDto> getInventoryByFilter(String sku, String name);

  List<InventoryItemResponseDto> getInventoryByStorageUnitId(Long id);

  List<InventoryItemResponseDto> getInventoryByWarehouseId(Long id);

  void deleteInventoryItem(Long id);
}
