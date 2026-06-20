package com.kml.services.inventory.service;

import com.kml.services.common.events.InventoryReservedEvent;
import com.kml.services.common.events.StockUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventPublisher {

    public static final String EXCHANGE = "kml.domain.events.exchange";
    public static final String INVENTORY_RESERVED_ROUTING_KEY = "inventory.reserved";
    public static final String STOCK_UPDATED_TOPIC = "inventory-stock-updates";

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, StockUpdatedEvent> kafkaTemplate;

    public InventoryEventPublisher(
        RabbitTemplate rabbitTemplate,
        KafkaTemplate<String, StockUpdatedEvent> kafkaTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInventoryReserved(InventoryReservedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, INVENTORY_RESERVED_ROUTING_KEY, event);
    }

    public void publishStockUpdated(StockUpdatedEvent event) {
        kafkaTemplate.send(STOCK_UPDATED_TOPIC, event.sku(), event);
    }
}
