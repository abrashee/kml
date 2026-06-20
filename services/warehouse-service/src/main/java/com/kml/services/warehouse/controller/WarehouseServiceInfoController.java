package com.kml.services.warehouse.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.ServiceInfo;
import com.kml.services.common.ServiceUrls;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseServiceInfoController {

    private final ServiceUrls serviceUrls;

    public WarehouseServiceInfoController(ServiceUrls serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    @GetMapping("/service-info")
    public ApiResponse<ServiceInfo> serviceInfo() {
        return ApiResponse.ok(
            ServiceInfo.ready(
                "kml-warehouse-service",
                "warehouse",
                Map.of(
                    "inventory", serviceUrls.inventoryUrl(),
                    "shipments", serviceUrls.shipmentUrl())));
    }
}
