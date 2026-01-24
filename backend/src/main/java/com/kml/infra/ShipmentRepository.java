package com.kml.infra;

import com.kml.domain.order.Order;
import com.kml.domain.shipment.Shipment;
import com.kml.domain.shipment.ShipmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
  List<Shipment> findByOrder(Order order);

  List<Shipment> findByStatus(ShipmentStatus status);
}
