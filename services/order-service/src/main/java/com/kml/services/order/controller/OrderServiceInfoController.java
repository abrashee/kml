package com.kml.services.order.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.ServiceInfo;
import com.kml.services.common.ServiceUrls;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderServiceInfoController {

    private final ServiceUrls serviceUrls;

    public OrderServiceInfoController(ServiceUrls serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    @GetMapping("/service-info")
    public ApiResponse<ServiceInfo> serviceInfo() {
        return ApiResponse.ok(
            ServiceInfo.ready(
                "kml-order-service",
                "order",
                Map.of(
                    "users", serviceUrls.userUrl(),
                    "inventory", serviceUrls.inventoryUrl(),
                    "shipments", serviceUrls.shipmentUrl())));
    }
}
