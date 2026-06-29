// src/main/java/com/kml/user/dto/UpdateStaffAccessDto.java
package com.kml.user.dto;

import com.kml.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public class UpdateStaffAccessDto {
    @NotNull
    private UserRole role;
    private Long warehouseId;
    // Keeping fields flexible if you want to use status fields down the road
    private String status;

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
