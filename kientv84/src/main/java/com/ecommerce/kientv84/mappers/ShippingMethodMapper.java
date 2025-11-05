package com.ecommerce.kientv84.mappers;

import com.ecommerce.kientv84.dtos.responses.ShippingMethodResponse;
import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShippingMethodMapper {
    ShippingMethodResponse mapToShippingMethodResponse(ShippingMethodEntity shippingMethod);
}
