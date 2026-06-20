package com.kml.services.inventory.repository;

import com.kml.services.inventory.entity.InventoryItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findBySku(String sku);

    List<InventoryItem> findByWarehouseId(Long warehouseId);

    Optional<InventoryItem> findBySkuAndStorageUnitId(String sku, Long storageUnitId);
}
