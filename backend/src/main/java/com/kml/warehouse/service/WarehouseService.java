package com.kml.warehouse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kml.user.entity.User;
import com.kml.warehouse.dto.WarehouseResponseDto;

public interface WarehouseService {

  WarehouseResponseDto createWarehouse(User owner, String name, String address);

  Optional<WarehouseResponseDto> getWarehouseById(Long id);

  Optional<WarehouseResponseDto> getWarehouseByName(String name);

  List<WarehouseResponseDto> getAllWarehouses();

  Page<WarehouseResponseDto> getAllWarehousesPage(String search, Pageable pageable);

  void enforceOwnership(Long warehouseId, User user);
}
