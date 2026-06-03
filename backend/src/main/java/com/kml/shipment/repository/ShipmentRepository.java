package com.kml.shipment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.shipment.entity.Shipment;
import com.kml.shipment.entity.ShipmentStatus;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

  List<Shipment> findByOrderId(Long orderId);

  List<Shipment> findByStatus(ShipmentStatus status);

  Page<Shipment> findAll(Pageable pageable);

  Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

  Page<Shipment> findByOrderId(Long orderId, Pageable pageable);
}
