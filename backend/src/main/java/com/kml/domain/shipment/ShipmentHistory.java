package com.kml.domain.shipment;

import java.time.LocalDateTime;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "shipment_history")
public class ShipmentHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "previous_status", nullable = false)
  private ShipmentStatus previousStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "new_status", nullable = false)
  private ShipmentStatus newStatus;

  @Column(name = "changed_at", nullable = false, updatable = false)
  private LocalDateTime changedAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "shipment_id", nullable = false)
  private Shipment shipment;

  // @ManyToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name = "user_id")
  // private User user;

  protected ShipmentHistory() {}

  public ShipmentHistory(
      Shipment shipment, ShipmentStatus previousStatus, ShipmentStatus newStatus) {
    validate(shipment, previousStatus, newStatus);
    this.shipment = shipment;
    this.previousStatus = previousStatus;
    this.newStatus = newStatus;
  }

  private static void validate(
      Shipment shipment, ShipmentStatus previousStatus, ShipmentStatus newStatus) {
    if (shipment == null) throw new IllegalArgumentException("Shipment is required");
    if (previousStatus == null) throw new IllegalArgumentException("Previous status is required");
    if (newStatus == null) throw new IllegalArgumentException("New status is required");
    if (previousStatus == newStatus) {
      throw new IllegalArgumentException("Previous status and new status must differ");
    }
  }

  @PrePersist
  protected void onCreate() {
    this.changedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public ShipmentStatus getPreviousStatus() {
    return previousStatus;
  }

  public ShipmentStatus getNewStatus() {
    return newStatus;
  }

  public LocalDateTime getChangedAt() {
    return changedAt;
  }

  public Shipment getShipment() {
    return shipment;
  }
}
