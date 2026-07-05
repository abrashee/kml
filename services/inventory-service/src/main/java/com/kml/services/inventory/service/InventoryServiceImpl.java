package com.kml.services.inventory.service;

import com.kml.services.common.events.StockUpdatedEvent;
import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import com.kml.services.inventory.entity.InventoryItem;
import com.kml.services.inventory.mapper.InventoryMapper;
import com.kml.services.inventory.repository.InventoryRepository;
import com.kml.services.inventory.search.InventorySearchIndexer;
import com.kml.services.inventory.search.InventorySearchService;
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
    private final InventorySearchService inventorySearchService;

    public InventoryServiceImpl(
        InventoryRepository inventoryRepository,
        InventoryEventPublisher eventPublisher,
        InventorySearchIndexer searchIndexer,
        InventorySearchService inventorySearchService) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
        this.searchIndexer = searchIndexer;
        this.inventorySearchService = inventorySearchService;
    }

    @Override
    @Transactional
    public InventoryItemResponseDto createInventoryItem(InventoryItemRequestDto request, JwtAuthenticatedUser principal) {
        requireWarehouseScope(request.warehouseId(), principal);
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
    public InventoryItemResponseDto adjustQuantity(Long id, int delta, JwtAuthenticatedUser principal) {
        InventoryItem item = inventoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        requireWarehouseScope(item.getWarehouseId(), principal);
        item.adjustQuantity(delta);
        InventoryItem saved = inventoryRepository.save(item);
        publish(saved);
        return InventoryMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponseDto getInventoryItem(Long id, JwtAuthenticatedUser principal) {
        InventoryItem item = inventoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        requireWarehouseScope(item.getWarehouseId(), principal);
        return InventoryMapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryItemResponseDto> getInventory(
        String sku,
        Long warehouseId,
        Pageable pageable,
        JwtAuthenticatedUser principal) {

        warehouseId = effectiveWarehouseId(warehouseId, principal);

        if (sku != null && !sku.isBlank()) {
            return inventorySearchService.search(sku, warehouseId, pageable);
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
    public void deleteInventoryItem(Long id, JwtAuthenticatedUser principal) {
        InventoryItem item = inventoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        requireWarehouseScope(item.getWarehouseId(), principal);
        if (item.getQuantity() > 0) {
            throw new IllegalArgumentException("Inventory item quantity must be zero before deletion");
        }
        inventoryRepository.delete(item);
    }

    private Long effectiveWarehouseId(Long requestedWarehouseId, JwtAuthenticatedUser principal) {
        if (isAdmin(principal)) {
            return requestedWarehouseId;
        }
        if (principal == null || principal.warehouseId() == null) {
            throw new SecurityException("Authenticated warehouse scope is missing");
        }
        if (requestedWarehouseId != null && !requestedWarehouseId.equals(principal.warehouseId())) {
            throw new SecurityException("User cannot access inventory outside assigned warehouse");
        }
        return principal.warehouseId();
    }

    private void requireWarehouseScope(Long warehouseId, JwtAuthenticatedUser principal) {
        if (isAdmin(principal)) {
            return;
        }
        if (principal == null || principal.warehouseId() == null || !principal.warehouseId().equals(warehouseId)) {
            throw new SecurityException("User cannot access inventory outside assigned warehouse");
        }
    }

    private boolean isAdmin(JwtAuthenticatedUser principal) {
        return principal != null && "ADMIN".equals(principal.role());
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
