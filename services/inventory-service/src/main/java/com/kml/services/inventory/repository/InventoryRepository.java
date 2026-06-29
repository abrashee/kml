package com.kml.services.inventory.repository;

import com.kml.services.inventory.entity.InventoryItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findBySku(String sku);

    Page<InventoryItem> findBySku(String sku, Pageable pageable);

    List<InventoryItem> findByWarehouseId(Long warehouseId);

    Page<InventoryItem> findByWarehouseId(Long warehouseId, Pageable pageable);

    Page<InventoryItem> findBySkuAndWarehouseId(String sku, Long warehouseId, Pageable pageable);

    Optional<InventoryItem> findBySkuAndStorageUnitId(String sku, Long storageUnitId);

    @Query("select coalesce(sum(i.quantity), 0) from InventoryItem i where i.sku = :sku")
    int sumQuantityBySku(@Param("sku") String sku);

    Optional<InventoryItem> findFirstBySkuOrderByQuantityDesc(String sku);
}
