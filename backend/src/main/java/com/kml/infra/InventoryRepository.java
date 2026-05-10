package com.kml.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.inventory.InventoryItem;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

  Optional<InventoryItem> findBySku(String sku);

  List<InventoryItem> findByName(String name);

  List<InventoryItem> findByQuantityBetween(int minQuantity, int maxQuantity);

  List<InventoryItem> findBySkuAndName(String sku, String name);
}
