package com.kml.capacity.service;

import java.util.List;
import java.util.Optional;

import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.domain.user.User;

public interface WarehouseService {

  // Create warehouse, returns DTO to avoid entity leak
  WarehouseResponseDto createWarehouse(User owner, String name, String address);

  // Get single warehouse by id, returns DTO
  Optional<WarehouseResponseDto> getWarehouseById(Long id);

  Optional<WarehouseResponseDto> getWarehouseByName(String name);

  // Get all warehouses for current user
  List<WarehouseResponseDto> getAllWarehouses();

  // Ownership check (internal)
  void enforceOwnership(Long warehouseId, User user);
}
