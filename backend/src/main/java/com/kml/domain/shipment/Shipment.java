package com.kml.domain.shipment;

import com.kml.capacity.dto.ai.RoutePlanDto;
import com.kml.domain.common.AuditableEntity;
import com.kml.domain.order.Order;
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
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class Shipment extends AuditableEntity {

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Transient private List<Object> domainEvents = new ArrayList<>();

  @Transient private RoutePlanDto lastRoutePlan;

  protected Shipment() {}

  private Shipment(User owner, Order order, String address, String carrierInfo, String tracking) {
    setOwner(owner);
    validateAddress(address);
    validateOrder(order);
    validateTracking(tracking);

    this.order = order;
    this.address = address;
    this.carrierInfo = carrierInfo;
    this.tracking = tracking;
    this.status = ShipmentStatus.PENDING;
  }

  public static Shipment createWithGeneratedTracking(
      User owner, Order order, String address, String carrierInfo) {
    return new Shipment(owner, order, address, carrierInfo, UUID.randomUUID().toString());
  }

  public static Shipment createWithExternalTracking(
      User owner, Order order, String address, String carrierInfo, String tracking) {
    return new Shipment(owner, order, address, carrierInfo, tracking);
  }

  public void transitionTo(ShipmentStatus nextStatus) {
    if (nextStatus == null) throw new IllegalArgumentException("Status is required");

    switch (this.status) {
      case PENDING -> {
        if (nextStatus != ShipmentStatus.IN_TRANSIT) throw invalidTransition(nextStatus);
      }
      case IN_TRANSIT -> {
        if (nextStatus != ShipmentStatus.DELIVERED) throw invalidTransition(nextStatus);
      }
      case DELIVERED -> {
        if (nextStatus != ShipmentStatus.RETURNED) throw invalidTransition(nextStatus);
      }
      case RETURNED ->
          throw new IllegalStateException("Returned shipment cannot transition further");
    }

    this.status = nextStatus;
    this.domainEvents.add(new ShipmentNotificationEvent(this.getOwner().getId(), this.id));
  }

  private IllegalStateException invalidTransition(ShipmentStatus nextStatus) {
    return new IllegalStateException(
        "Invalid status transition from " + this.status + " to " + nextStatus);
  }

  private void validateAddress(String address) {
    if (address == null || address.isBlank())
      throw new IllegalArgumentException("Address is required");
  }

  private void validateOrder(Order order) {
    if (order == null) throw new IllegalArgumentException("Order is required");
  }

  private void validateTracking(String tracking) {
    if (tracking == null || tracking.isBlank())
      throw new IllegalArgumentException("Tracking info is required");
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

  public Order getOrder() {
    return order;
  }

  public List<Object> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  public void clearDomainEvents() {
    domainEvents.clear();
  }

  public RoutePlanDto getLastRoutePlan() {
    return lastRoutePlan;
  }

  public void setLastRoutePlan(RoutePlanDto lastRoutePlan) {
    this.lastRoutePlan = lastRoutePlan;
  }
}
