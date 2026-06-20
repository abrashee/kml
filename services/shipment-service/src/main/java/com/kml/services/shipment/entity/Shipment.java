package com.kml.services.shipment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tracking_code", nullable = false, unique = true)
    private String trackingCode;

    @Column(nullable = false)
    private String address;

    @Column(name = "carrier_info")
    private String carrierInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Shipment() {
    }

    public Shipment(Long orderId, Long warehouseId, Long userId, String trackingCode, String address, String carrierInfo) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (trackingCode == null || trackingCode.isBlank()) {
            throw new IllegalArgumentException("Tracking code is required");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        this.orderId = orderId;
        this.warehouseId = warehouseId;
        this.userId = userId;
        this.trackingCode = trackingCode;
        this.address = address;
        this.carrierInfo = carrierInfo;
    }

    public void transitionTo(ShipmentStatus nextStatus) {
        if (nextStatus == null) {
            throw new IllegalArgumentException("Shipment status is required");
        }
        boolean valid = switch (this.status) {
            case PENDING -> nextStatus == ShipmentStatus.IN_TRANSIT || nextStatus == ShipmentStatus.RETURNED;
            case IN_TRANSIT -> nextStatus == ShipmentStatus.DELIVERED || nextStatus == ShipmentStatus.RETURNED;
            case DELIVERED -> nextStatus == ShipmentStatus.RETURNED;
            case RETURNED -> false;
        };
        if (!valid) {
            throw new IllegalStateException("Invalid status transition from " + this.status + " to " + nextStatus);
        }
        this.status = nextStatus;
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

    public Long getOrderId() {
        return orderId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public String getAddress() {
        return address;
    }

    public String getCarrierInfo() {
        return carrierInfo;
    }

    public ShipmentStatus getStatus() {
        return status;
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
