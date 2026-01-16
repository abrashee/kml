package com.kml.infra;

import com.kml.domain.inventory.InventoryItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

  // Search by SKU
  Optional<InventoryItem> findBySku(String sku);

  // Search by exact name
  List<InventoryItem> findByName(String name);

  // Filter by Quantity Range
  List<InventoryItem> findByQuantityBetween(int minQuantity, int maxQuantity);

  // Combined Filters (Name + SKU)
  List<InventoryItem> findBySkuAndName(String sku, String name);
}
