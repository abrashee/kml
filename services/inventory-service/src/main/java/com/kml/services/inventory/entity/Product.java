package com.kml.services.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean searchable = true;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "primary_warehouse_id")
    private Long primaryWarehouseId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Product() {}

    public Product(String sku, String name) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        this.sku = sku;
        this.name = name;
    }

    public void updateProjection(String name, int availableQuantity, Long primaryWarehouseId) {
        if (name != null && !name.isBlank()) this.name = name;
        this.availableQuantity = Math.max(availableQuantity, 0);
        this.primaryWarehouseId = primaryWarehouseId;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public boolean isActive() { return active; }
    public boolean isSearchable() { return searchable; }
    public int getAvailableQuantity() { return availableQuantity; }
    public Long getPrimaryWarehouseId() { return primaryWarehouseId; }
}
