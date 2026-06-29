package com.kml.services.shipment.service;

import com.kml.services.common.events.ShipmentCreatedEvent;
import com.kml.services.shipment.dto.ShipmentRequestDto;
import com.kml.services.shipment.dto.ShipmentResponseDto;
import com.kml.services.shipment.entity.Shipment;
import com.kml.services.shipment.entity.ShipmentHistory;
import com.kml.services.shipment.entity.ShipmentStatus;
import com.kml.services.shipment.mapper.ShipmentMapper;
import com.kml.services.shipment.repository.ShipmentHistoryRepository;
import com.kml.services.shipment.repository.ShipmentRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentHistoryRepository shipmentHistoryRepository;
    private final ShipmentEventPublisher eventPublisher;
    private final Counter shipmentsCreatedCounter;

    public ShipmentServiceImpl(
        ShipmentRepository shipmentRepository,
        ShipmentHistoryRepository shipmentHistoryRepository,
        ShipmentEventPublisher eventPublisher,
        MeterRegistry meterRegistry) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentHistoryRepository = shipmentHistoryRepository;
        this.eventPublisher = eventPublisher;
        this.shipmentsCreatedCounter = Counter.builder("kml_shipments_created")
            .description("Total number of successfully created shipments")
            .register(meterRegistry);
    }

    @Override
    @Transactional
    public ShipmentResponseDto createShipment(ShipmentRequestDto request) {
        String trackingCode = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Shipment shipment = new Shipment(
            request.orderId(),
            request.warehouseId(),
            request.userId(),
            trackingCode,
            request.address(),
            request.carrierInfo());
        Shipment saved = shipmentRepository.save(shipment);
        shipmentHistoryRepository.save(new ShipmentHistory(saved, null, saved.getStatus(), "Shipment created"));
        eventPublisher.publishShipmentCreated(new ShipmentCreatedEvent(
            saved.getId(),
            saved.getOrderId(),
            saved.getWarehouseId(),
            saved.getTrackingCode(),
            Instant.now()));
        shipmentsCreatedCounter.increment();
        return ShipmentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponseDto getShipment(Long id) {
        return shipmentRepository.findById(id)
            .map(ShipmentMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipments(Long orderId, Long userId, ShipmentStatus status) {
        List<Shipment> shipments;
        if (orderId != null) {
            shipments = shipmentRepository.findByOrderId(orderId);
        } else if (userId != null) {
            shipments = shipmentRepository.findByUserId(userId);
        } else if (status != null) {
            shipments = shipmentRepository.findByStatus(status);
        } else {
            shipments = shipmentRepository.findAll();
        }
        return shipments.stream().map(ShipmentMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ShipmentResponseDto updateStatus(Long id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
        ShipmentStatus previous = shipment.getStatus();
        shipment.transitionTo(status);
        Shipment saved = shipmentRepository.save(shipment);
        shipmentHistoryRepository.save(new ShipmentHistory(saved, previous, status, "Shipment status updated"));
        return ShipmentMapper.toDto(saved);
    }
}
