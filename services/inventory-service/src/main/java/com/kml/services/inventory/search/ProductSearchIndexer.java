package com.kml.services.inventory.search;

import com.kml.services.inventory.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductSearchIndexer {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchIndexer.class);

    private final RestClient restClient;
    private final OpenSearchProperties properties;

    public ProductSearchIndexer(
        RestClient.Builder restClientBuilder,
        OpenSearchProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.url()).build();
    }

    public void index(Product product) {
        try {
            restClient.put()
                .uri("/{index}/_doc/{id}", properties.productsIndex(), product.getId())
                .body(new ProductSearchDocument(
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getAvailableQuantity(),
                    product.getPrimaryWarehouseId(),
                    product.isActive(),
                    product.isSearchable()))
                .retrieve()
                .toBodilessEntity();
        } catch (RuntimeException ex) {
            log.warn("Failed to index product {} into OpenSearch", product.getId(), ex);
        }
    }

    public record ProductSearchDocument(
        Long id,
        String sku,
        String name,
        String description,
        java.math.BigDecimal price,
        int availableQuantity,
        Long primaryWarehouseId,
        boolean active,
        boolean searchable) {
    }
}