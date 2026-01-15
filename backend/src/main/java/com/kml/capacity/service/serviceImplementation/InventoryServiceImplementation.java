package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.infra.InventoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImplementation implements InventoryService {

  private final InventoryRepository inventoryRepository;

  public InventoryServiceImplementation(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
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

  // Delete Inventory
}
