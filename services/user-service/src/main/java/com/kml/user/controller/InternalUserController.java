package com.kml.user.controller;

import com.kml.user.dto.UserResponseDto;
import com.kml.user.service.UserService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/v1/users")
public class InternalUserController {

    private final UserService userService;
    private final byte[] internalToken;

    public InternalUserController(
        UserService userService,
        @Value("${kml.internal-service-token}") String internalToken) {
        this.userService = userService;
        this.internalToken = internalToken.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{userId}/shipping-address")
    public ShippingAddressResponse getShippingAddress(
        @PathVariable Long userId,
        @RequestHeader(value = "X-KML-Internal-Token", required = false) String suppliedToken) {
        byte[] supplied = suppliedToken == null
            ? new byte[0]
            : suppliedToken.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(internalToken, supplied)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        UserResponseDto user = userService.getUserById(userId);
        return new ShippingAddressResponse(user.id(), user.address());
    }

    public record ShippingAddressResponse(Long userId, String address) {
    }
}
