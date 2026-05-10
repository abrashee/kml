package com.kml.capacity.service.impl;

import com.kml.api.error.OwnershipException;
import com.kml.capacity.dto.warehouse.WarehouseResponseDto;
import com.kml.capacity.mapper.WarehouseMapper;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.user.User;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.WarehouseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouseRepository;
  private final CurrentUserProvider currentUserProvider;

  public WarehouseServiceImpl(
      WarehouseRepository warehouseRepository, CurrentUserProvider currentUserProvider) {
    this.warehouseRepository = warehouseRepository;
    this.currentUserProvider = currentUserProvider;
  }

  @Override
  @Transactional
  public WarehouseResponseDto createWarehouse(User owner, String name, String address) {

    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Warehouse name is required");
    if (address == null || address.isBlank())
      throw new IllegalArgumentException("Warehouse address is required");

    Warehouse warehouse = Warehouse.create(owner, name, address);
    Warehouse saved = warehouseRepository.save(warehouse);

    return WarehouseMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<WarehouseResponseDto> getWarehouseById(Long id) {
    Warehouse warehouse =
        warehouseRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    enforceOwnership(id, currentUserProvider.getCurrentUser());
    return Optional.of(WarehouseMapper.toDto(warehouse));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<WarehouseResponseDto> getWarehouseByName(String name) {
    Warehouse warehouse =
        warehouseRepository
            .findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    enforceOwnership(warehouse.getId(), currentUserProvider.getCurrentUser());
    return Optional.of(WarehouseMapper.toDto(warehouse));
  }

  @Override
  @Transactional(readOnly = true)
  public List<WarehouseResponseDto> getAllWarehouses() {
    User currentUser = currentUserProvider.getCurrentUser();
    return warehouseRepository.findAll().stream()
        .filter(w -> w.getOwner().getId().equals(currentUser.getId()))
        .map(WarehouseMapper::toDto)
        .toList();
  }

  @Override
  public void enforceOwnership(Long warehouseId, User user) {
    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    if (!warehouse.getOwner().getId().equals(user.getId())) {
      throw new OwnershipException("User does not own this warehouse");
    }
  }
}
