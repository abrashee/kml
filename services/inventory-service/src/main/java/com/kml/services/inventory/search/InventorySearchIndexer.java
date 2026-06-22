package com.kml.services.inventory.search;

import com.kml.services.inventory.entity.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class InventorySearchIndexer {

    private static final Logger log = LoggerFactory.getLogger(InventorySearchIndexer.class);

    private final RestClient restClient;
    private final OpenSearchProperties properties;

    public InventorySearchIndexer(RestClient.Builder restClientBuilder, OpenSearchProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.url()).build();
    }

    public void index(InventoryItem item) {
        try {
            restClient.put()
                .uri("/{index}/_doc/{id}", properties.inventoryIndex(), item.getId())
                .body(new InventorySearchDocument(
                    item.getId(),
                    item.getSku(),
                    item.getName(),
                    item.getQuantity(),
                    item.getWarehouseId(),
                    item.getStorageUnitId()))
                .retrieve()
                .toBodilessEntity();
        } catch (RuntimeException ex) {
            log.warn("Failed to index inventory item {} into OpenSearch", item.getId(), ex);
        }
    }

    public record InventorySearchDocument(
        Long id,
        String sku,
        String name,
        int quantity,
        Long warehouseId,
        Long storageUnitId) {
    }
}
