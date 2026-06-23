package com.kml.services.inventory.search;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kml.opensearch")
public record OpenSearchProperties(
    String url,
    String inventoryIndex,
    String productsIndex) {
}
