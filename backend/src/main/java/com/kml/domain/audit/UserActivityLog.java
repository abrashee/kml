package com.kml.domain.audit;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_activity_log")
public class UserActivityLog extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "action", nullable = false)
  private String action;

  @Column(name = "entity", nullable = false)
  private String entity;

  @Column(name = "entity_id")
  private Long entityId;

  protected UserActivityLog() {}

  public UserActivityLog(User owner, User user, String action, String entity, Long entityId) {
    setOwner(owner);
    if (user == null) throw new IllegalArgumentException("User must not be null");
    if (action == null || action.isBlank())
      throw new IllegalArgumentException("Action must not be blank");
    if (entity == null || entity.isBlank())
      throw new IllegalArgumentException("Entity must not be blank");

    this.user = user;
    this.action = action;
    this.entity = entity;
    this.entityId = entityId;
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
}
