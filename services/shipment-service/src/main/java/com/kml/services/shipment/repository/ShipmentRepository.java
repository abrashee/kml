package com.kml.services.shipment.repository;

import com.kml.services.shipment.entity.Shipment;
import com.kml.services.shipment.entity.ShipmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingCode(String trackingCode);

    List<Shipment> findByOrderId(Long orderId);

    List<Shipment> findByUserId(Long userId);

    List<Shipment> findByStatus(ShipmentStatus status);
}
