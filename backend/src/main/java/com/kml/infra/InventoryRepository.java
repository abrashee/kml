package com.kml.infra;

import com.kml.domain.inventory.InventoryItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
  Optional<InventoryItem> findBySku(String sku);
}
