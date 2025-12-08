package com.ecommerce.kientv84.repositories;

import com.ecommerce.kientv84.entities.OrderEntity;
import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethodEntity, UUID> , JpaSpecificationExecutor<ShippingMethodEntity> {
    ShippingMethodEntity findShippingMethodByShippingCode(String code);

    @Query(value = """
        SELECT * FROM shipping_methods_entity
        WHERE document_tsv @@ to_tsquery('simple', :q || ':*')
        ORDER BY ts_rank(document_tsv, to_tsquery('simple', :q || ':*')) DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<ShippingMethodEntity> searchShippingMethodSuggestion(@Param("q") String q, @Param("limit") int limit);
}
