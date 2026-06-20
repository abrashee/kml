package com.kml.services.shipment.repository;

import com.kml.services.shipment.entity.ShipmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, Long> {
}
