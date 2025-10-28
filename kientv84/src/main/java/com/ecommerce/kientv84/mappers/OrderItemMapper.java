package com.ecommerce.kientv84.mappers;

import com.ecommerce.kientv84.dtos.responses.OrderItemResponse;
import com.ecommerce.kientv84.entities.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse mapToOrderItemResponse(OrderItemEntity orderItemEntity);
}
