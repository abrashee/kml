package com.kml.services.inventory.config;

import com.kml.services.inventory.service.InventoryEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryMessagingConfig {

    public static final String ORDER_PLACED_QUEUE = "inventory.order-placed";
    public static final String ORDER_PLACED_ROUTING_KEY = "order.placed";
    public static final String DEAD_LETTER_EXCHANGE = "kml.domain.events.dlx";
    public static final String ORDER_PLACED_DEAD_LETTER_QUEUE = "inventory.order-placed.dlq";
    public static final String ORDER_PLACED_DEAD_LETTER_ROUTING_KEY = "inventory.order-placed.failed";

    @Bean
    public DirectExchange domainEventsExchange() {
        return new DirectExchange(InventoryEventPublisher.EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderPlacedQueue() {
        return QueueBuilder.durable(ORDER_PLACED_QUEUE)
            .deadLetterExchange(DEAD_LETTER_EXCHANGE)
            .deadLetterRoutingKey(ORDER_PLACED_DEAD_LETTER_ROUTING_KEY)
            .build();
    }

    @Bean
    public Queue orderPlacedDeadLetterQueue() {
        return QueueBuilder.durable(ORDER_PLACED_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding orderPlacedBinding(Queue orderPlacedQueue, DirectExchange domainEventsExchange) {
        return BindingBuilder.bind(orderPlacedQueue)
            .to(domainEventsExchange)
            .with(ORDER_PLACED_ROUTING_KEY);
    }

    @Bean
    public Binding orderPlacedDeadLetterBinding(
        Queue orderPlacedDeadLetterQueue,
        DirectExchange deadLetterExchange
    ) {
        return BindingBuilder.bind(orderPlacedDeadLetterQueue)
            .to(deadLetterExchange)
            .with(ORDER_PLACED_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
