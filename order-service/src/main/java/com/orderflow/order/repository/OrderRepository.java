package com.orderflow.order.repository;

import com.orderflow.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    @EntityGraph(attributePaths = "lines")
    Optional<OrderEntity> findByOrderId(String orderId);
}
