package com.kml.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.order.entity.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

  Optional<OrderStatus> findByName(String name);
}
