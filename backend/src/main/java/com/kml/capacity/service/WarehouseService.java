package com.kml.capacity.service;

import java.util.List;
import java.util.Optional;

import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.domain.user.User;

public interface WarehouseService {

  WarehouseResponseDto createWarehouse(User owner, String name, String address);

  Optional<WarehouseResponseDto> getWarehouseById(Long id);

  Optional<WarehouseResponseDto> getWarehouseByName(String name);

  List<WarehouseResponseDto> getAllWarehouses();

  void enforceOwnership(Long warehouseId, User user);
}
