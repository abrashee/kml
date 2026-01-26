package com.kml.domain.shipment;

import com.kml.domain.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class Shipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tracking", nullable = false, unique = true)
  private String tracking;

  @Column(name = "carrier_info")
  private String carrierInfo;

  @Column(name = "address", nullable = false)
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ShipmentStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  protected Shipment() {}

  public Shipment(String tracking, String carrierInfo, String address, Order order) {
    validateAddress(address);
    validateOrder(order);
    validateTracking(tracking);

    this.tracking = tracking;
    this.carrierInfo = carrierInfo;
    this.address = address;
    this.status = ShipmentStatus.PENDING;
    this.order = order;
  }

  public static Shipment createWithGeneratedTracking(
      Order order, String address, String carrierInfo) {
    String tracking = UUID.randomUUID().toString();
    return new Shipment(tracking, carrierInfo, address, order);
  }

  public static Shipment createWithExternalTracking(
      Order order, String address, String carrierInfo, String tracking) {
    return new Shipment(tracking, carrierInfo, address, order);
  }

  public void transitionTo(ShipmentStatus nextStatus) {
    if (nextStatus == null) {
      throw new IllegalArgumentException("Status is required");
    }

    if (this.status == ShipmentStatus.PENDING && nextStatus == ShipmentStatus.IN_TRANSIT) {
      this.status = nextStatus;
      return;
    }

    if (this.status == ShipmentStatus.IN_TRANSIT && nextStatus == ShipmentStatus.DELIVERED) {
      this.status = nextStatus;
      return;
    }

    if (nextStatus == ShipmentStatus.DELIVERED && nextStatus == ShipmentStatus.RETURNED) {
      this.status = nextStatus;
      return;
    }

    throw new IllegalStateException(
        "Invalid status transition from " + this.status + " to " + nextStatus);
  }

  private void validateAddress(String address) {
    if (address == null || address.isBlank()) {
      throw new IllegalArgumentException("Address is required");
    }
  }

  private void validateOrder(Order order) {
    if (order == null) {
      throw new IllegalArgumentException("Order is required");
    }
  }

  private void validateTracking(String tracking) {
    if (tracking == null || tracking.isBlank()) {
      throw new IllegalArgumentException("Tracking info is required");
    }
  }

  @PrePersist
  public void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public String getTracking() {
    return tracking;
  }

  public String getCarrierInfo() {
    return carrierInfo;
  }

  public String getAddress() {
    return address;
  }

  public ShipmentStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public Order getOrder() {
    return order;
  }
}
