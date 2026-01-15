package com.kml.capacity.service.serviceImplemenation;

import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.infra.InventoryRepository;
import jakarta.transaction.Transactional;
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

  // Get All Inventory

  // Get Inventory by SKU

  // Get Inventory by Id

  // Get All Inventories

  // Delete Inventory
}
