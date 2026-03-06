package com.kml.domain.audit;

import com.kml.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log")
public class UserActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private final User user;

  @Column(name = "action", nullable = false)
  private final String action;

  @Column(name = "entity", nullable = false)
  private final String entity;

  @Column(name = "entity_id")
  private final Long entityId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected UserActivityLog() {
    this.user = null;
    this.action = null;
    this.entity = null;
    this.entityId = null;
  }

  public UserActivityLog(User user, String action, String entity, Long entityId) {
    if (user == null) {
      throw new IllegalArgumentException("User must not be null");
    }
    if (action == null || action.isBlank()) {
      throw new IllegalArgumentException("Action must not be blank");
    }
    if (entity == null || entity.isBlank()) {
      throw new IllegalArgumentException("Entity must not be blank");
    }
    this.user = user;
    this.action = action;
    this.entity = entity;
    this.entityId = entityId;
  }

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getAction() {
    return action;
  }

  public String getEntity() {
    return entity;
  }

  public Long getEntityId() {
    return entityId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
