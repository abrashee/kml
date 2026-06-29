package com.kml.services.order.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.order.dto.OrderItemsUpdateRequestDto;
import com.kml.services.order.dto.OrderRequestDto;
import com.kml.services.order.dto.OrderResponseDto;
import com.kml.services.order.entity.OrderStatus;
import com.kml.services.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto request, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(orderService.createOrder(request, principal), "Order created");
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponseDto> getOrder(@PathVariable Long id, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(orderService.getOrder(id, principal));
    }

    @GetMapping
    public ApiResponse<List<OrderResponseDto>> getOrders(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) OrderStatus status,
        @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(orderService.getOrders(userId, status, principal));
    }


    @PatchMapping("/{id}/items")
    public ApiResponse<OrderResponseDto> updateItems(
        @PathVariable Long id,
        @Valid @RequestBody OrderItemsUpdateRequestDto request,
        @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(orderService.updateItems(id, request, principal), "Order items updated");
    }

    @PatchMapping("/{id}/status/{status}")
    public ApiResponse<OrderResponseDto> updateStatus(@PathVariable Long id, @PathVariable OrderStatus status, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(orderService.updateStatus(id, status, principal), "Order status updated");
    }

    @PatchMapping("/{id}/worker/{workerId}")
    public ApiResponse<OrderResponseDto> assignWorker(@PathVariable Long id, @PathVariable Long workerId, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(orderService.assignWorker(id, workerId, principal), "Order worker assigned");
    }
}
