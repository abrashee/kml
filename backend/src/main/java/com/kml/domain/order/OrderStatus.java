package com.kml.domain.order;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_statuses")
public class OrderStatus extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column private String description;

  protected OrderStatus() {}

  public OrderStatus(User owner, String name, String description) {
    setOwner(owner);
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Order status name cannot be empty");
    }
    this.name = name;
    this.description = description;
  }

  public static OrderStatus create(User owner, String name, String description) {
    return new OrderStatus(owner, name, description);
  }

  public void update(String name, String description) {
    if (name != null && !name.isBlank()) {
      this.name = name;
    }
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
