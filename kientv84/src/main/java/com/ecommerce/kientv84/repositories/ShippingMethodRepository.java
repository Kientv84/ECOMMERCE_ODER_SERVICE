package com.ecommerce.kientv84.repositories;

import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethodEntity, UUID> {
}
