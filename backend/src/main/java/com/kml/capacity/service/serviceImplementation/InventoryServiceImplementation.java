package com.kml.capacity.service.serviceImplementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.WarehouseRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImplementation implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final WarehouseRepository warehouseRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository;

  public InventoryServiceImplementation(
      InventoryRepository inventoryRepository,
      WarehouseRepository warehouseRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository) {
    this.inventoryRepository = inventoryRepository;
    this.warehouseRepository = warehouseRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.storageUnitInventoryAssignmentRepository = storageUnitInventoryAssignmentRepository;
  }

  // Create Inventory

  // Update Inventory
  @Override
  @Transactional
  public InventoryItem updateQuantity(String sku, int delta) {
    InventoryItem item =
        inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    if (delta > 0) {
      item.increaseQuantity(delta);
    } else if (delta < 0) {
      item.decreaseQuantity(-delta);
    }

    return inventoryRepository.save(item);
  }

  // Get All Inventories
  @Override
  public List<InventoryItem> getAllInventories() {
    List<InventoryItem> inventoryItems = this.inventoryRepository.findAll();
    return inventoryItems;
  }

  // Get Inventory by SKU
  @Override
  public InventoryItem getInventoryBySku(String sku) {
    InventoryItem inventoryItem =
        this.inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    return inventoryItem;
  }

  // Get Inventory by Id
  @Override
  public InventoryItem getInventoryById(Long id) {
    InventoryItem inventoryItem =
        this.inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

    return inventoryItem;
  }

  // Search by Name
  @Override
  public List<InventoryItem> getInventoryByName(String name) {
    return inventoryRepository.findByName(name);
  }

  // Filter by Quantity Range
  @Override
  public List<InventoryItem> getInventoryByRange(int minQuantity, int maxQuantity) {
    return inventoryRepository.findByQuantityBetween(minQuantity, maxQuantity);
  }

  // | Combined Filters (SKU + name)
  @Override
  public List<InventoryItem> getInventoryByFilter(String sku, String name) {
    return inventoryRepository.findBySkuAndName(sku, name);
  }

  @Override
  public List<InventoryItem> getInventoryByStorageUnitId(Long storageUnitId) {
    List<StorageUnitInventoryAssignment> assignments =
        this.storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnitId);

    List<InventoryItem> inventoryItems = new ArrayList<>();
    for (StorageUnitInventoryAssignment assignment : assignments) {
      inventoryItems.add(assignment.getInventoryItem());
    }

    return inventoryItems;
  }

  @Override
  public List<InventoryItem> getInventoryByWarehouseId(Long warehouseId) {

    List<InventoryItem> inventoryItems = new ArrayList<>();

    List<StorageUnit> storageUnits = this.storageUnitRepository.findByWarehouse_Id(warehouseId);

    for (StorageUnit storageUnit : storageUnits) {
      List<StorageUnitInventoryAssignment> assignments =
          this.storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnit.getId());

      for (StorageUnitInventoryAssignment assignment : assignments) {
        inventoryItems.add(assignment.getInventoryItem());
      }
    }

    return inventoryItems;
  }

  // Delete Inventory
}
