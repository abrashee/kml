package com.kml.services.inventory.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kml.services.common.events.InventoryReservedEvent;
import com.kml.services.common.events.OrderPlacedEvent;
import com.kml.services.common.events.StockUpdatedEvent;
import com.kml.services.inventory.entity.InventoryItem;
import com.kml.services.inventory.repository.InventoryRepository;
import com.kml.services.inventory.search.InventorySearchIndexer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class OrderPlacedEventListenerTest {

    private final InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
    private final InventoryEventPublisher eventPublisher = Mockito.mock(InventoryEventPublisher.class);
    private final InventorySearchIndexer searchIndexer = Mockito.mock(InventorySearchIndexer.class);
    private final OrderPlacedEventListener listener =
        new OrderPlacedEventListener(inventoryRepository, eventPublisher, searchIndexer, new SimpleMeterRegistry());

    @Test
    void reservesInventoryAndPublishesReservationEvent() {
        InventoryItem item = new InventoryItem(1L, "SKU-1", "Part", 5, 10L, 20L, 1, 1);
        when(inventoryRepository.findBySku("SKU-1")).thenReturn(List.of(item));
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        listener.reserveInventory(new OrderPlacedEvent(
            100L,
            200L,
            "12 Test Street, Berlin",
            List.of(new OrderPlacedEvent.OrderLine("SKU-1", 2, 10L)),
            Instant.now()));

        ArgumentCaptor<InventoryReservedEvent> reservation =
            ArgumentCaptor.forClass(InventoryReservedEvent.class);
        verify(searchIndexer).index(any(InventoryItem.class));
        verify(eventPublisher).publishStockUpdated(any(StockUpdatedEvent.class));
        verify(eventPublisher).publishInventoryReserved(reservation.capture());

        InventoryReservedEvent event = reservation.getValue();
        assertEquals(100L, event.orderId());
        assertEquals(200L, event.userId());
        assertEquals("12 Test Street, Berlin", event.shippingAddress());
        assertEquals(10L, event.warehouseId());
        assertEquals(2, event.lines().getFirst().quantity());
    }
}
