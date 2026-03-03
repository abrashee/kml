package com.kml.capacity.service.impl;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.mapper.InventoryMapper;
import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import java.util.ArrayList;
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
    boolean exists = inventoryRepository.findBySku(sku).isPresent();
    if (exists) {
      throw new IllegalArgumentException("Inventory item with SKU already exists");
    }

    InventoryItem item = new InventoryItem(sku, name, quantity);
    InventoryItem saved = inventoryRepository.save(item);
    return InventoryMapper.toDto(saved);
  }

  @Override
  @Transactional
  public InventoryItemResponseDto updateQuantity(String sku, int delta) {
    InventoryItem item =
        inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    if (delta > 0) {
      item.increaseQuantity(delta);
    } else if (delta < 0) {
      item.decreaseQuantity(-delta);
    }

    InventoryItem saved = inventoryRepository.save(item);

    return InventoryMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getAllInventories() {
    List<InventoryItem> inventoryItems = this.inventoryRepository.findAll();
    List<InventoryItemResponseDto> dtoResponseItems = new ArrayList<>();
    for (InventoryItem item : inventoryItems) {
      dtoResponseItems.add(InventoryMapper.toDto(item));
    }
    return dtoResponseItems;
  }

  @Override
  @Transactional(readOnly = true)
  public InventoryItemResponseDto getInventoryBySku(String sku) {
    InventoryItem inventoryItem =
        this.inventoryRepository
            .findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    return InventoryMapper.toDto(inventoryItem);
  }

  @Override
  @Transactional(readOnly = true)
  public InventoryItemResponseDto getInventoryById(Long id) {
    InventoryItem inventoryItem =
        this.inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

    return InventoryMapper.toDto(inventoryItem);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByName(String name) {
    List<InventoryItem> items = inventoryRepository.findByName(name);
    List<InventoryItemResponseDto> dtoResponseItems = new ArrayList<>();
    for (InventoryItem item : items) {
      dtoResponseItems.add(InventoryMapper.toDto(item));
    }
    return dtoResponseItems;
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByRange(int minQuantity, int maxQuantity) {
    List<InventoryItem> items = inventoryRepository.findByQuantityBetween(minQuantity, maxQuantity);
    List<InventoryItemResponseDto> dtoResponseItems = new ArrayList<>();
    for (InventoryItem item : items) {
      dtoResponseItems.add(InventoryMapper.toDto(item));
    }
    return dtoResponseItems;
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByFilter(String sku, String name) {
    List<InventoryItem> items = inventoryRepository.findBySkuAndName(sku, name);
    List<InventoryItemResponseDto> dtoResponseItems = new ArrayList<>();
    for (InventoryItem item : items) {
      dtoResponseItems.add(InventoryMapper.toDto(item));
    }
    return dtoResponseItems;
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByStorageUnitId(Long storageUnitId) {
    List<StorageUnitInventoryAssignment> assignments =
        this.storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnitId);

    List<InventoryItem> inventoryItems = new ArrayList<>();
    for (StorageUnitInventoryAssignment assignment : assignments) {
      inventoryItems.add(assignment.getInventoryItem());
    }

    List<InventoryItemResponseDto> dtoResponseItems = new ArrayList<>();
    for (InventoryItem item : inventoryItems) {
      dtoResponseItems.add(InventoryMapper.toDto(item));
    }
    return dtoResponseItems;
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoryByWarehouseId(Long warehouseId) {
    List<InventoryItem> inventoryItems = new ArrayList<>();
    List<StorageUnit> storageUnits = this.storageUnitRepository.findByWarehouse_Id(warehouseId);
    for (StorageUnit storageUnit : storageUnits) {
      List<StorageUnitInventoryAssignment> assignments =
          this.storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnit.getId());

      for (StorageUnitInventoryAssignment assignment : assignments) {
        inventoryItems.add(assignment.getInventoryItem());
      }
    }

    List<InventoryItemResponseDto> dtoResponseItems = new ArrayList<>();
    for (InventoryItem item : inventoryItems) {
      dtoResponseItems.add(InventoryMapper.toDto(item));
    }
    return dtoResponseItems;
  }

  @Override
  @Transactional(readOnly = true)
  public List<InventoryItemResponseDto> getInventoriesFiltered(
      String sku, String name, Integer minQuantity, Integer maxQuantity) {

    List<InventoryItem> filteredItems = new ArrayList<>();

    if (sku != null && name != null) {
      filteredItems = inventoryRepository.findBySkuAndName(sku, name);
    } else if (sku != null) {
      filteredItems = inventoryRepository.findBySku(sku).map(List::of).orElse(List.of());
    } else if (name != null) {
      filteredItems = inventoryRepository.findByName(name);
    } else if (minQuantity != null && maxQuantity != null) {
      filteredItems = inventoryRepository.findByQuantityBetween(minQuantity, maxQuantity);
    } else {
      filteredItems = inventoryRepository.findAll();
    }

    List<InventoryItemResponseDto> dtos = new ArrayList<>();
    for (InventoryItem item : filteredItems) {
      dtos.add(InventoryMapper.toDto(item));
    }
    return dtos;
  }

  @Override
  @Transactional
  public void deleteInventoryItem(Long id) {
    InventoryItem item =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    if (item.getQuantity() > 0) {
      throw new IllegalStateException("Cannot delete inventory with remaining quantity");
    }

    if (storageUnitInventoryAssignmentRepository.existsByInventoryItem_Id(id)) {
      throw new IllegalArgumentException("Cannot delete inventory item assigned to storage units");
    }

    inventoryRepository.delete(item);
  }
}
