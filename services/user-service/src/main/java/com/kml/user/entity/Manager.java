package com.kml.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Manager extends User {

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    protected Manager() {}

    public Manager(String name, String username, String password, Long warehouseId) {
        super(name, username, password, UserRole.MANAGER);
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
}