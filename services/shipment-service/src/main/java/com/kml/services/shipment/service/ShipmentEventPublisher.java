package com.kml.services.shipment.service;

import com.kml.services.common.events.ShipmentCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventPublisher {

    public static final String EXCHANGE = "kml.domain.events.exchange";
    public static final String SHIPMENT_CREATED_ROUTING_KEY = "shipment.created";

    private final RabbitTemplate rabbitTemplate;

    public ShipmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishShipmentCreated(ShipmentCreatedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, SHIPMENT_CREATED_ROUTING_KEY, event);
    }
}
