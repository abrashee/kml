package com.kml.services.user.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.ServiceInfo;
import com.kml.services.common.ServiceUrls;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserServiceInfoController {

    private final ServiceUrls serviceUrls;

    public UserServiceInfoController(ServiceUrls serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    @GetMapping("/service-info")
    public ApiResponse<ServiceInfo> serviceInfo() {
        return ApiResponse.ok(
            ServiceInfo.ready(
                "kml-user-service",
                "user",
                Map.of(
                    "orders", serviceUrls.orderUrl(),
                    "shipments", serviceUrls.shipmentUrl())));
    }
}
