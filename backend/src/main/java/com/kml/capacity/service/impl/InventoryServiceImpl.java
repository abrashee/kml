package com.kml.capacity.service.impl;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.mapper.InventoryMapper;
import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository;

  public InventoryServiceImpl(
      InventoryRepository inventoryRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository) {
    this.inventoryRepository = inventoryRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.storageUnitInventoryAssignmentRepository = storageUnitInventoryAssignmentRepository;
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

    InventoryItem saved = inventoryRepository.save(new InventoryItem(sku, name, quantity));
    return InventoryMapper.toDto(saved);
  }

  @Override
  @Transactional
  public InventoryItemResponseDto updateQuantity(String sku, int delta) {
    InventoryItem item =
        inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    if (delta > 0) item.increaseQuantity(delta);
    if (delta < 0) item.decreaseQuantity(Math.abs(delta));

    return InventoryMapper.toDto(inventoryRepository.save(item));
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getAllInventories() {
    return inventoryRepository.findAll().stream().map(InventoryMapper::toDto).toList();
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
    return inventoryRepository.findByName(name).stream().map(InventoryMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByRange(int min, int max) {
    return inventoryRepository.findByQuantityBetween(min, max).stream()
        .map(InventoryMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByFilter(String sku, String name) {
    List<InventoryItem> items =
        (sku != null && name != null) ? inventoryRepository.findBySkuAndName(sku, name) : List.of();
    return items.stream().map(InventoryMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByStorageUnitId(Long storageUnitId) {
    return storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnitId).stream()
        .map(assignment -> InventoryMapper.toDto(assignment.getInventoryItem()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByWarehouseId(Long warehouseId) {
    return storageUnitRepository.findByWarehouse_Id(warehouseId).stream()
        .flatMap(
            storageUnit ->
                storageUnitInventoryAssignmentRepository
                    .findByStorageUnit_Id(storageUnit.getId())
                    .stream())
        .map(assignment -> InventoryMapper.toDto(assignment.getInventoryItem()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoriesFiltered(
      String sku, String name, Integer min, Integer max) {
    List<InventoryItem> filtered;

    if (sku != null && name != null) filtered = inventoryRepository.findBySkuAndName(sku, name);
    else if (sku != null)
      filtered = inventoryRepository.findBySku(sku).map(List::of).orElse(List.of());
    else if (name != null) filtered = inventoryRepository.findByName(name);
    else if (min != null && max != null)
      filtered = inventoryRepository.findByQuantityBetween(min, max);
    else filtered = inventoryRepository.findAll();

    return filtered.stream().map(InventoryMapper::toDto).toList();
  }

  @Override
  @Transactional
  public void deleteInventoryItem(Long id) {
    InventoryItem item =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    if (item.getQuantity() > 0)
      throw new IllegalStateException("Cannot delete inventory with remaining quantity");
    if (storageUnitInventoryAssignmentRepository.existsByInventoryItem_Id(id))
      throw new IllegalArgumentException("Cannot delete inventory item assigned to storage units");

    inventoryRepository.delete(item);
  }
}
