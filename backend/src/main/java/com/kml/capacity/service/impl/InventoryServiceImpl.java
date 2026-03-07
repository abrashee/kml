package com.kml.capacity.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.api.error.OwnershipException;
import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.mapper.InventoryMapper;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.InventoryService;
import com.kml.domain.audit.UserActivityLog;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.user.User;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.UserActivityLogRepository;

@Service
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;
  private final UserActivityLogRepository activityLogRepository;
  private final CurrentUserProvider currentUserProvider;

  public InventoryServiceImpl(
      InventoryRepository inventoryRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository,
      UserActivityLogRepository activityLogRepository,
      CurrentUserProvider currentUserProvider) {

    this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
    this.storageUnitRepository = Objects.requireNonNull(storageUnitRepository);
    this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
    this.activityLogRepository = Objects.requireNonNull(activityLogRepository);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  // Helper to log user actions
  private void logActivity(String action, InventoryItem item) {
    User currentUser = currentUserProvider.getCurrentUser();
    UserActivityLog log =
        new UserActivityLog(
            item.getOwner(),
            currentUser,
            action,
            InventoryItem.class.getSimpleName(),
            item.getId());
    activityLogRepository.save(log);
  }

  @Override
  @Transactional
  public InventoryItemResponseDto createInventoryItem(String sku, String name, int quantity) {
    inventoryRepository
        .findBySku(sku)
        .ifPresent(
            i -> {
              throw new IllegalArgumentException("Inventory item with SKU already exists");
            });

    User currentUser = currentUserProvider.getCurrentUser();
    InventoryItem item = InventoryItem.create(sku, name, quantity, currentUser);
    InventoryItem saved = inventoryRepository.save(item);

    logActivity("CREATE", saved);
    return InventoryMapper.toDto(saved);
  }

  @Override
  @Transactional
  public InventoryItemResponseDto updateQuantity(String sku, int delta) {
    InventoryItem item =
        inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    // Ownership enforcement
    User currentUser = currentUserProvider.getCurrentUser();
    if (!item.getOwner().getId().equals(currentUser.getId())) {
      throw new OwnershipException("Cannot modify inventory not owned by current user");
    }

    if (delta > 0) item.increaseQuantity(delta);
    if (delta < 0) item.decreaseQuantity(Math.abs(delta));

    InventoryItem saved = inventoryRepository.save(item);
    logActivity("UPDATE_QUANTITY", saved);
    return InventoryMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getAllInventories() {
    return inventoryRepository.findAll().stream()
        .map(InventoryMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public InventoryItemResponseDto getInventoryBySku(String sku) {
    InventoryItem item =
        inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
    return InventoryMapper.toDto(item);
  }

  @Override
  @Transactional(readOnly = true)
  public InventoryItemResponseDto getInventoryById(Long id) {
    InventoryItem item =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
    return InventoryMapper.toDto(item);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByName(String name) {
    return inventoryRepository.findByName(name).stream()
        .map(InventoryMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByRange(int minQuantity, int maxQuantity) {
    return inventoryRepository.findByQuantityBetween(minQuantity, maxQuantity).stream()
        .map(InventoryMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByFilter(String sku, String name) {
    List<InventoryItem> items =
        (sku != null && name != null) ? inventoryRepository.findBySkuAndName(sku, name) : List.of();
    return items.stream().map(InventoryMapper::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByStorageUnitId(Long storageUnitId) {
    return assignmentRepository.findByStorageUnit_Id(storageUnitId).stream()
        .map(a -> InventoryMapper.toDto(a.getInventoryItem()))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByWarehouseId(Long warehouseId) {
    return storageUnitRepository.findByWarehouse_Id(warehouseId).stream()
        .flatMap(su -> assignmentRepository.findByStorageUnit_Id(su.getId()).stream())
        .map(a -> InventoryMapper.toDto(a.getInventoryItem()))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoriesFiltered(
      String sku, String name, Integer minQuantity, Integer maxQuantity) {
    List<InventoryItem> filtered;
    if (sku != null && name != null) filtered = inventoryRepository.findBySkuAndName(sku, name);
    else if (sku != null)
      filtered = inventoryRepository.findBySku(sku).map(List::of).orElse(List.of());
    else if (name != null) filtered = inventoryRepository.findByName(name);
    else if (minQuantity != null && maxQuantity != null)
      filtered = inventoryRepository.findByQuantityBetween(minQuantity, maxQuantity);
    else filtered = inventoryRepository.findAll();

    return filtered.stream().map(InventoryMapper::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteInventoryItem(Long id) {
    InventoryItem item =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    // Ownership enforcement
    User currentUser = currentUserProvider.getCurrentUser();
    if (!item.getOwner().getId().equals(currentUser.getId())) {
      throw new OwnershipException("Cannot delete inventory not owned by current user");
    }

    if (item.getQuantity() > 0)
      throw new IllegalStateException("Cannot delete inventory with remaining quantity");
    if (assignmentRepository.existsByInventoryItem_Id(id))
      throw new IllegalArgumentException("Cannot delete inventory item assigned to storage units");

    inventoryRepository.delete(item);
    logActivity("DELETE", item);
  }
}
