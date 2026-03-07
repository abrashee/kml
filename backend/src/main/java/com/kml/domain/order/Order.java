package com.kml.domain.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  @ManyToOne(optional = false)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  protected Order() {}

  public Order(User owner, String code, OrderStatus status) {
    setOwner(owner);
    validateCode(code);
    validateStatus(status);
    this.code = code;
    this.status = status;
  }

  public static Order create(User owner, String code, OrderStatus status) {
    return new Order(owner, code, status);
  }

  public void addItem(OrderItem item) {
    if (item == null) throw new IllegalArgumentException("Item cannot be null");
    item.setOrder(this);
    this.items.add(item);
  }

  public void replaceItems(List<OrderItem> newItems) {
    this.items.clear();
    if (newItems != null) newItems.forEach(this::addItem);
  }

  private void validateCode(String code) {
    if (code == null || code.isBlank()) throw new IllegalArgumentException("Order code required");
  }

  private void validateStatus(OrderStatus status) {
    if (status == null) throw new IllegalArgumentException("Order status required");
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
  }
}
