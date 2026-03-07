package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kml.domain.shipment.ShipmentHistory;

@Repository
public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, Long> {

  // Order by createdAt (from AuditableEntity) instead of changedAt
  List<ShipmentHistory> findByShipment_IdOrderByCreatedAtAsc(Long shipmentId);
}
