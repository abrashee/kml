package com.kml.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.inventory.entity.InventoryItem;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

  Optional<InventoryItem> findBySku(String sku);

  List<InventoryItem> findByName(String name);

  List<InventoryItem> findByQuantityBetween(int minQuantity, int maxQuantity);

  List<InventoryItem> findBySkuAndName(String sku, String name);

  Page<InventoryItem> findBySkuContainingIgnoreCaseOrNameContainingIgnoreCase(
      String sku, String name, Pageable pageable);

  Page<InventoryItem> findAll(Pageable pageable);
}
