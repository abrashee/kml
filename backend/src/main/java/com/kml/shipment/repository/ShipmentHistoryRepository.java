package com.kml.shipment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kml.shipment.entity.ShipmentHistory;

@Repository
public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, Long> {

  List<ShipmentHistory> findByShipment_IdOrderByCreatedAtAsc(Long shipmentId);
}
