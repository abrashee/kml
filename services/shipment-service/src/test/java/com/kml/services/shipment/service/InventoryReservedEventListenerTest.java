package com.kml.services.shipment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kml.services.common.events.InventoryReservedEvent;
import com.kml.services.shipment.dto.ShipmentRequestDto;
import com.kml.services.shipment.entity.Shipment;
import com.kml.services.shipment.repository.ShipmentRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class InventoryReservedEventListenerTest {

    private final ShipmentRepository shipmentRepository = Mockito.mock(ShipmentRepository.class);
    private final ShipmentService shipmentService = Mockito.mock(ShipmentService.class);
    private final InventoryReservedEventListener listener =
        new InventoryReservedEventListener(shipmentRepository, shipmentService);

    @Test
    void createsShipmentWhenReservationIsNew() {
        when(shipmentRepository.findByOrderId(100L)).thenReturn(List.of());

        listener.createShipmentForReservation(new InventoryReservedEvent(
            100L,
            200L,
            "12 Test Street, Berlin",
            10L,
            List.of(new InventoryReservedEvent.ReservationLine("SKU-1", 2)),
            Instant.now()));

        ArgumentCaptor<ShipmentRequestDto> request = ArgumentCaptor.forClass(ShipmentRequestDto.class);
        verify(shipmentService).createShipment(request.capture());

        assertEquals(100L, request.getValue().orderId());
        assertEquals(200L, request.getValue().userId());
        assertEquals(10L, request.getValue().warehouseId());
        assertEquals("12 Test Street, Berlin", request.getValue().address());
    }

    @Test
    void skipsDuplicateShipmentForOrder() {
        when(shipmentRepository.findByOrderId(100L))
            .thenReturn(List.of(new Shipment(100L, 10L, 200L, "TRK-TEST", "Address", "Carrier")));

        listener.createShipmentForReservation(new InventoryReservedEvent(
            100L,
            200L,
            "12 Test Street, Berlin",
            10L,
            List.of(new InventoryReservedEvent.ReservationLine("SKU-1", 2)),
            Instant.now()));

        verify(shipmentService, never()).createShipment(any(ShipmentRequestDto.class));
    }
}
