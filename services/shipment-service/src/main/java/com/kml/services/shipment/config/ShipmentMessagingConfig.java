package com.kml.services.shipment.config;

import com.kml.services.shipment.service.ShipmentEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShipmentMessagingConfig {

    public static final String INVENTORY_RESERVED_QUEUE = "shipment.inventory-reserved";
    public static final String INVENTORY_RESERVED_ROUTING_KEY = "inventory.reserved";
    public static final String DEAD_LETTER_EXCHANGE = "kml.domain.events.dlx";
    public static final String INVENTORY_RESERVED_DEAD_LETTER_QUEUE = "shipment.inventory-reserved.dlq";
    public static final String INVENTORY_RESERVED_DEAD_LETTER_ROUTING_KEY = "shipment.inventory-reserved.failed";

    @Bean
    public DirectExchange domainEventsExchange() {
        return new DirectExchange(ShipmentEventPublisher.EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue inventoryReservedQueue() {
        return QueueBuilder.durable(INVENTORY_RESERVED_QUEUE)
            .deadLetterExchange(DEAD_LETTER_EXCHANGE)
            .deadLetterRoutingKey(INVENTORY_RESERVED_DEAD_LETTER_ROUTING_KEY)
            .build();
    }

    @Bean
    public Queue inventoryReservedDeadLetterQueue() {
        return QueueBuilder.durable(INVENTORY_RESERVED_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding inventoryReservedBinding(Queue inventoryReservedQueue, DirectExchange domainEventsExchange) {
        return BindingBuilder.bind(inventoryReservedQueue)
            .to(domainEventsExchange)
            .with(INVENTORY_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryReservedDeadLetterBinding(
        Queue inventoryReservedDeadLetterQueue,
        DirectExchange deadLetterExchange
    ) {
        return BindingBuilder.bind(inventoryReservedDeadLetterQueue)
            .to(deadLetterExchange)
            .with(INVENTORY_RESERVED_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageRecoverer shipmentMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(
            rabbitTemplate,
            DEAD_LETTER_EXCHANGE,
            INVENTORY_RESERVED_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public RetryOperationsInterceptor shipmentRetryInterceptor(MessageRecoverer shipmentMessageRecoverer) {
        return RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000)
            .recoverer(shipmentMessageRecoverer)
            .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory shipmentRabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        MessageConverter rabbitMessageConverter,
        RetryOperationsInterceptor shipmentRetryInterceptor) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        factory.setAdviceChain(shipmentRetryInterceptor);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
