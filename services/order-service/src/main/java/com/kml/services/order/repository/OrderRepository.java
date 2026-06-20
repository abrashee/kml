package com.kml.services.order.repository;

import com.kml.services.order.entity.Order;
import com.kml.services.order.entity.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByCode(String code);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);
}
