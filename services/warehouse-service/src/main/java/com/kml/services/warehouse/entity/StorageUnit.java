package com.kml.services.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "storage_units")
public class StorageUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "remaining_capacity", nullable = false)
    private int remainingCapacity;

    protected StorageUnit() {
    }

    public StorageUnit(String code, int capacity) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Storage unit code is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Storage unit capacity must be positive");
        }
        this.code = code;
        this.capacity = capacity;
        this.remainingCapacity = capacity;
    }

    void attachTo(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void reserveCapacity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > remainingCapacity) {
            throw new IllegalArgumentException("Storage unit capacity exceeded");
        }
        this.remainingCapacity -= quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getWarehouseId() {
        return warehouse != null ? warehouse.getId() : null;
    }

    public String getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRemainingCapacity() {
        return remainingCapacity;
    }
}
