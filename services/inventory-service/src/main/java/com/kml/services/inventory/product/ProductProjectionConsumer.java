package com.kml.services.inventory.product;

import com.kml.services.common.events.StockUpdatedEvent;
import com.kml.services.inventory.service.InventoryEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductProjectionConsumer {

    private final ProductProjectionService projectionService;

    public ProductProjectionConsumer(ProductProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @KafkaListener(topics = InventoryEventPublisher.STOCK_UPDATED_TOPIC)
    public void onStockUpdated(StockUpdatedEvent event) {
        projectionService.rebuildSku(event.sku());
    }
}
