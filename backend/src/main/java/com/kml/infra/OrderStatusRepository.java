package com.kml.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.order.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

  Optional<OrderStatus> findByName(String name);
}
