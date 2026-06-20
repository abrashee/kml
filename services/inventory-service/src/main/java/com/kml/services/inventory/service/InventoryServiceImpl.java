package com.kml.services.inventory.service;

import com.kml.services.common.events.StockUpdatedEvent;
import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import com.kml.services.inventory.entity.InventoryItem;
import com.kml.services.inventory.mapper.InventoryMapper;
import com.kml.services.inventory.repository.InventoryRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventPublisher eventPublisher;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public InventoryItemResponseDto createInventoryItem(InventoryItemRequestDto request) {
        InventoryItem item = inventoryRepository.findBySkuAndStorageUnitId(request.sku(), request.storageUnitId())
            .orElseGet(() -> new InventoryItem(
                request.ownerUserId(),
                request.sku(),
                request.name(),
                0,
                request.warehouseId(),
                request.storageUnitId(),
                request.reorderThreshold(),
                request.safetyStockLevel()));
        item.adjustQuantity(request.quantity());
        InventoryItem saved = inventoryRepository.save(item);
        publish(saved);
        return InventoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public InventoryItemResponseDto adjustQuantity(Long id, int delta) {
        InventoryItem item = inventoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        item.adjustQuantity(delta);
        InventoryItem saved = inventoryRepository.save(item);
        publish(saved);
        return InventoryMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponseDto getInventoryItem(Long id) {
        return inventoryRepository.findById(id)
            .map(InventoryMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponseDto> getInventory(String sku, Long warehouseId) {
        List<InventoryItem> items;
        if (sku != null && !sku.isBlank()) {
            items = inventoryRepository.findBySku(sku);
        } else if (warehouseId != null) {
            items = inventoryRepository.findByWarehouseId(warehouseId);
        } else {
            items = inventoryRepository.findAll();
        }
        return items.stream().map(InventoryMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteInventoryItem(Long id) {
        InventoryItem item = inventoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        if (item.getQuantity() > 0) {
            throw new IllegalArgumentException("Inventory item quantity must be zero before deletion");
        }
        inventoryRepository.delete(item);
    }

    private void publish(InventoryItem item) {
        eventPublisher.publishStockUpdated(new StockUpdatedEvent(
            item.getId(),
            item.getSku(),
            item.getQuantity(),
            item.getWarehouseId(),
            Instant.now()));
    }
}
