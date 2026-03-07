package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kml.domain.shipment.ShipmentHistory;

@Repository
public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, Long> {

  List<ShipmentHistory> findByShipment_IdOrderByCreatedAtAsc(Long shipmentId);
}
