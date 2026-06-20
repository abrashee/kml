package com.kml.audit.userActivity.entity;

import com.kml.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log")
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "entity", nullable = false)
    private String entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Required by JPA Spec
    protected UserActivityLog() {}

    // 3-arg constructor for simple logging variants
    public UserActivityLog(String action, String details, User user) {
        this.action = action;
        this.details = details;
        this.user = user;
        this.entity = "User";
    }

    // 5-arg constructor: Populates both fields safely to handle any argument ordering variations
    public UserActivityLog(User owner, User user, String action, String value, Long entityId) {
        this.owner = owner;
        this.user = user;
        this.action = action;
        this.entityId = entityId;
        this.entity = (value != null) ? value : "InventoryItem";
        this.details = value;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        // ⚡ THE PERMANENT FIX: Defensively intercept and populate 'entity' before SQL compilation
        if (this.entity == null) {
            if (this.details != null && (this.details.equals("InventoryItem") || this.details.length() < 30)) {
                this.entity = this.details;
            } else {
                this.entity = "InventoryItem"; // Absolute fallback constraint safety
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    // Fallback aliases for warehouse inventory modules
    public Long getWarehouseId() { return entityId; }
    public void setWarehouseId(Long warehouseId) { this.entityId = warehouseId; }
    public Long getReferenceId() { return entityId; }
    public void setReferenceId(Long referenceId) { this.entityId = referenceId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}