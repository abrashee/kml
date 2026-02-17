package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImplementation implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository;

  public InventoryServiceImplementation(
      InventoryRepository inventoryRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository) {
    this.inventoryRepository = inventoryRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.storageUnitInventoryAssignmentRepository = storageUnitInventoryAssignmentRepository;
  }

  // Create Inventory
  @Override
  @Transactional
  public InventoryItem createInventoryItem(String sku, String name, int quantity) {
    boolean exists = inventoryRepository.findBySku(sku).isPresent();
    if (exists) {
      throw new IllegalArgumentException("Inventory item with SKU alrady exists");
    }

    InventoryItem item = new InventoryItem(sku, name, quantity);
    return inventoryRepository.save(item);
  }

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
  @Override
  @Transactional
  public void deleteInventoryItem(Long id) {
    InventoryItem item =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    if (storageUnitInventoryAssignmentRepository.existsByInventoryItem_Id(id)) {
      throw new IllegalArgumentException("Cannot delete inventory item assigned to storage units");
    }
    if (item.getQuantity() > 0) {
      throw new IllegalStateException("Cannot delete inventory with remaining quantity");
    }

    inventoryRepository.delete(item);
  }
}
