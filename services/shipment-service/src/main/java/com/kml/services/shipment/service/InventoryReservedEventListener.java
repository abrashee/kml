package com.kml.services.shipment.service;

import com.kml.services.common.events.InventoryReservedEvent;
import com.kml.services.shipment.config.ShipmentMessagingConfig;
import com.kml.services.shipment.dto.ShipmentRequestDto;
import com.kml.services.shipment.repository.ShipmentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventoryReservedEventListener {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentService shipmentService;

    public InventoryReservedEventListener(
        ShipmentRepository shipmentRepository,
        ShipmentService shipmentService) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentService = shipmentService;
    }

    @RabbitListener(queues = ShipmentMessagingConfig.INVENTORY_RESERVED_QUEUE, containerFactory = "shipmentRabbitListenerContainerFactory")
    @Transactional
    public void createShipmentForReservation(InventoryReservedEvent event) {
        if (!shipmentRepository.findByOrderId(event.orderId()).isEmpty()) {
            return;
        }

        shipmentService.createShipment(new ShipmentRequestDto(
            event.orderId(),
            event.warehouseId(),
            event.userId(),
            event.shippingAddress(),
            "ASYNC_FULFILLMENT"));
    }
}
