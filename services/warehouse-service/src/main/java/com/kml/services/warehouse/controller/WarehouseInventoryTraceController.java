package com.kml.services.warehouse.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.ServiceUrls;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseInventoryTraceController {

    private final RestClient restClient;
    private final ServiceUrls serviceUrls;

    public WarehouseInventoryTraceController(RestClient.Builder restClientBuilder, ServiceUrls serviceUrls) {
        this.restClient = restClientBuilder.build();
        this.serviceUrls = serviceUrls;
    }

    @GetMapping("/inventory-trace")
    public ApiResponse<Map<String, Object>> traceInventory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        Object inventoryServiceInfo = restClient.get()
            .uri(serviceUrls.inventoryUrl() + "/api/v1/inventories/service-info")
            .header(HttpHeaders.AUTHORIZATION, authorization)
            .retrieve()
            .body(Object.class);

        return ApiResponse.ok(Map.of(
            "warehouseService", "kml-warehouse-service",
            "inventoryService", serviceUrls.inventoryUrl(),
            "inventoryServiceInfo", inventoryServiceInfo));
    }
}
