package com.kml.services.warehouse.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StorageUnit> storageUnits = new ArrayList<>();

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Warehouse() {
    }

    public Warehouse(Long ownerUserId, String name, String address) {
        if (ownerUserId == null) {
            throw new IllegalArgumentException("Owner user ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name is required");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Warehouse address is required");
        }
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.address = address;
    }

    public void addStorageUnit(StorageUnit storageUnit) {
        storageUnit.attachTo(this);
        this.storageUnits.add(storageUnit);
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

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<StorageUnit> getStorageUnits() {
        return Collections.unmodifiableList(storageUnits);
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
