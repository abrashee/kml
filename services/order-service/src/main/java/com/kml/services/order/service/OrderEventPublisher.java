package com.kml.services.order.service;

import com.kml.services.common.events.OrderPlacedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    public static final String EXCHANGE = "kml.domain.events.exchange";
    public static final String ORDER_PLACED_ROUTING_KEY = "order.placed";

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderPlaced(OrderPlacedEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, ORDER_PLACED_ROUTING_KEY, event);
    }
}
