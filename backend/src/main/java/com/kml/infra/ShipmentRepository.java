package com.kml.infra;

import com.kml.domain.shipment.Shipment;
import com.kml.domain.shipment.ShipmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
  List<Shipment> findByOrderId(Long orderId);

  List<Shipment> findByStatus(ShipmentStatus status);

  Optional<Shipment> findByTracking(String tracking);
}
