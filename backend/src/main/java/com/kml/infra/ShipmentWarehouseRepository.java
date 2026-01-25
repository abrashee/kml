package com.kml.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kml.domain.warehouse.Warehouse;

public interface ShipmentWarehouseRepository extends JpaRepository<Warehouse, Long> {
  @Query(
      """
          SELECT DISTINCT w
          FROM Warehouse w
          JOIN w.storageUnits su
          JOIN StorageUnitInventoryAssignment sia ON sia.storageUnit = su
          JOIN sia.inventoryItem ii
          JOIN OrderItem oi ON oi.inventoryItem = ii
          JOIN Order o ON o.id = oi.order.id
          JOIN Shipment s ON s.order.id = o.id
          WHERE s.id = :shipmentId
      """)
  List<Warehouse> findWarehouseByShipmentId(@Param("shipmentId") Long shipmentId);
}
