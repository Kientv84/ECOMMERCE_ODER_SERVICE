package com.ecommerce.kientv84.repositories;

import com.ecommerce.kientv84.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    OrderEntity findOrderByOrderCode(String code);
}
