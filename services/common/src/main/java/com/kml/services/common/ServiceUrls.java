package com.kml.services.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kml.services")
public record ServiceUrls(
    String userUrl,
    String orderUrl,
    String inventoryUrl,
    String warehouseUrl,
    String shipmentUrl) {
}
