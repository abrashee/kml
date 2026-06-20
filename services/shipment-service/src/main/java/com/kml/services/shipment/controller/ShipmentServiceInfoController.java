package com.kml.services.shipment.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.ServiceInfo;
import com.kml.services.common.ServiceUrls;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentServiceInfoController {

    private final ServiceUrls serviceUrls;

    public ShipmentServiceInfoController(ServiceUrls serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    @GetMapping("/service-info")
    public ApiResponse<ServiceInfo> serviceInfo() {
        return ApiResponse.ok(
            ServiceInfo.ready(
                "kml-shipment-service",
                "shipment",
                Map.of(
                    "orders", serviceUrls.orderUrl(),
                    "warehouses", serviceUrls.warehouseUrl())));
    }
}
