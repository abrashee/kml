package com.kml.services.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(nullable = false, length = 80)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "storage_unit_id", nullable = false)
    private Long storageUnitId;

    @Column(name = "reorder_threshold", nullable = false)
    private int reorderThreshold;

    @Column(name = "safety_stock_level", nullable = false)
    private int safetyStockLevel;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected InventoryItem() {
    }

    public InventoryItem(
        Long ownerUserId,
        String sku,
        String name,
        int quantity,
        Long warehouseId,
        Long storageUnitId,
        int reorderThreshold,
        int safetyStockLevel) {
        if (ownerUserId == null) {
            throw new IllegalArgumentException("Owner user ID is required");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID is required");
        }
        if (storageUnitId == null) {
            throw new IllegalArgumentException("Storage unit ID is required");
        }
        validateQuantity(quantity);
        this.ownerUserId = ownerUserId;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.warehouseId = warehouseId;
        this.storageUnitId = storageUnitId;
        this.reorderThreshold = Math.max(reorderThreshold, 0);
        this.safetyStockLevel = Math.max(safetyStockLevel, 0);
    }

    public void adjustQuantity(int delta) {
        int nextQuantity = this.quantity + delta;
        validateQuantity(nextQuantity);
        this.quantity = nextQuantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Inventory quantity cannot be negative");
        }
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public Long getStorageUnitId() {
        return storageUnitId;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public int getSafetyStockLevel() {
        return safetyStockLevel;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
