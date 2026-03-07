package com.kml.domain.shipment;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.user.User;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "shipment_history")
public class ShipmentHistory extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "previous_status", nullable = false)
  private ShipmentStatus previousStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "new_status", nullable = false)
  private ShipmentStatus newStatus;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "shipment_id", nullable = false)
  private Shipment shipment;

  protected ShipmentHistory() {}

  public ShipmentHistory(
      User owner, Shipment shipment, ShipmentStatus previousStatus, ShipmentStatus newStatus) {
    setOwner(owner);
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
    if (previousStatus == newStatus)
      throw new IllegalArgumentException("Previous and new status must differ");
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

  public Shipment getShipment() {
    return shipment;
  }
}
