package com.kml.services.inventory.service;

import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    InventoryItemResponseDto createInventoryItem(InventoryItemRequestDto request, JwtAuthenticatedUser principal);

    InventoryItemResponseDto adjustQuantity(Long id, int delta, JwtAuthenticatedUser principal);

    InventoryItemResponseDto getInventoryItem(Long id, JwtAuthenticatedUser principal);

    Page<InventoryItemResponseDto> getInventory(String sku, Long warehouseId, Pageable pageable, JwtAuthenticatedUser principal);

    void deleteInventoryItem(Long id, JwtAuthenticatedUser principal);
}
