package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.shipment.ShipmentHistory;

public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, Long> {
  List<ShipmentHistory> findByShipment_IdOrderByChangedAtAsc(Long shipmentId);
}
