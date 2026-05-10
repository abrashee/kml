package com.kml.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
