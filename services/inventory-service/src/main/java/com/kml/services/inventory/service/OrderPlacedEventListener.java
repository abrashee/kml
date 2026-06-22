package com.kml.services.inventory.service;

import com.kml.services.common.events.InventoryReservedEvent;
import com.kml.services.common.events.OrderPlacedEvent;
import com.kml.services.common.events.StockUpdatedEvent;
import com.kml.services.inventory.config.InventoryMessagingConfig;
import com.kml.services.inventory.entity.InventoryItem;
import com.kml.services.inventory.repository.InventoryRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderPlacedEventListener {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventPublisher eventPublisher;

    public OrderPlacedEventListener(
        InventoryRepository inventoryRepository,
        InventoryEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    @RabbitListener(queues = InventoryMessagingConfig.ORDER_PLACED_QUEUE)
    @Transactional
    public void reserveInventory(OrderPlacedEvent event) {
        List<InventoryReservedEvent.ReservationLine> reservedLines = new ArrayList<>();
        Long warehouseId = null;

        for (OrderPlacedEvent.OrderLine line : event.lines()) {
            InventoryItem item = inventoryRepository.findBySku(line.sku()).stream()
                .filter(candidate -> line.warehouseId() == null || line.warehouseId().equals(candidate.getWarehouseId()))
                .filter(candidate -> candidate.getQuantity() >= line.quantity())
                .sorted(Comparator.comparing(InventoryItem::getQuantity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not enough inventory for SKU " + line.sku()));

            item.adjustQuantity(-line.quantity());
            InventoryItem saved = inventoryRepository.save(item);
            if (warehouseId == null) {
                warehouseId = saved.getWarehouseId();
            }
            reservedLines.add(new InventoryReservedEvent.ReservationLine(line.sku(), line.quantity()));
            eventPublisher.publishStockUpdated(new StockUpdatedEvent(
                saved.getId(),
                saved.getSku(),
                saved.getQuantity(),
                saved.getWarehouseId(),
                Instant.now()));
        }

        eventPublisher.publishInventoryReserved(new InventoryReservedEvent(
            event.orderId(),
            event.userId(),
            event.shippingAddress(),
            warehouseId,
            reservedLines,
            Instant.now()));
    }
}
