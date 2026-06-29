package com.kml.services.shipment.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.shipment.dto.ShipmentRequestDto;
import com.kml.services.shipment.dto.ShipmentResponseDto;
import com.kml.services.shipment.entity.ShipmentStatus;
import com.kml.services.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ShipmentResponseDto> createShipment(@Valid @RequestBody ShipmentRequestDto request, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(shipmentService.createShipment(request, principal), "Shipment created");
    }

    @GetMapping("/{id}")
    public ApiResponse<ShipmentResponseDto> getShipment(@PathVariable Long id, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(shipmentService.getShipment(id, principal));
    }

    @GetMapping
    public ApiResponse<List<ShipmentResponseDto>> getShipments(
        @RequestParam(required = false) Long orderId,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) ShipmentStatus status,
        @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(shipmentService.getShipments(orderId, userId, status, principal));
    }

    @PatchMapping("/{id}/status/{status}")
    public ApiResponse<ShipmentResponseDto> updateStatus(@PathVariable Long id, @PathVariable ShipmentStatus status, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(shipmentService.updateStatus(id, status, principal), "Shipment status updated");
    }
}
