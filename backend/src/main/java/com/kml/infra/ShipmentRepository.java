package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.shipment.Shipment;
import com.kml.domain.shipment.ShipmentStatus;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

  List<Shipment> findByOrderId(Long orderId);

  List<Shipment> findByStatus(ShipmentStatus status);
}
