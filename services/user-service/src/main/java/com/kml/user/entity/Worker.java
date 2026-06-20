package com.kml.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Worker extends User {

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "manager_id")
    private Long managerId;

    protected Worker() {}

    public Worker(String name, String username, String password, Long warehouseId, Long managerId) {
        super(name, username, password, UserRole.WORKER);
        this.warehouseId = warehouseId;
        this.managerId = managerId;
    }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
}