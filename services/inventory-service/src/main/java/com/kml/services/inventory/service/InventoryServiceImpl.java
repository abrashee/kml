package com.kml.services.inventory.service;

import com.kml.services.common.events.StockUpdatedEvent;
import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import com.kml.services.inventory.entity.InventoryItem;
import com.kml.services.inventory.mapper.InventoryMapper;
import com.kml.services.inventory.repository.InventoryRepository;
import com.kml.services.inventory.search.InventorySearchIndexer;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventPublisher eventPublisher;
    private final InventorySearchIndexer searchIndexer;

    public InventoryServiceImpl(
        InventoryRepository inventoryRepository,
        InventoryEventPublisher eventPublisher,
        InventorySearchIndexer searchIndexer) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
        this.searchIndexer = searchIndexer;
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
    public Page<InventoryItemResponseDto> getInventory(
        String sku,
        Long warehouseId,
        Pageable pageable) {

        if (sku != null && !sku.isBlank()) {
            return inventoryRepository
                .findBySku(sku, pageable)
                .map(InventoryMapper::toDto);
        }

        if (warehouseId != null) {
            return inventoryRepository
                .findByWarehouseId(warehouseId, pageable)
                .map(InventoryMapper::toDto);
        }

        return inventoryRepository
            .findAll(pageable)
            .map(InventoryMapper::toDto);
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
        searchIndexer.index(item);
        eventPublisher.publishStockUpdated(new StockUpdatedEvent(
            item.getId(),
            item.getSku(),
            item.getQuantity(),
            item.getWarehouseId(),
            Instant.now()));
    }
}
