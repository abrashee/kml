package com.kml.services.order.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userProfileClient", url = "${kml.services.user-url}")
public interface UserProfileClient {

    @GetMapping("/internal/v1/users/{userId}/shipping-address")
    ShippingAddressResponse getShippingAddress(
        @PathVariable Long userId,
        @RequestHeader("X-KML-Internal-Token") String internalToken);

    record ShippingAddressResponse(Long userId, String address) {
    }
}
