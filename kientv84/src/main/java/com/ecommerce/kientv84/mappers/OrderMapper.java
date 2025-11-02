package com.ecommerce.kientv84.mappers;

import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.ecommerce.kientv84.entities.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse mapToOrderResponse(OrderEntity orderEntity);

    KafkaOrderResponse mapToKafkaOrderResponse(OrderResponse orderResponse);
}
