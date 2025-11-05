package com.ecommerce.kientv84.mappers;

import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.ecommerce.kientv84.dtos.responses.kafka.KafkaOrderShippingResponse;
import com.ecommerce.kientv84.entities.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse mapToOrderResponse(OrderEntity orderEntity);

    @Mapping(target = "shippingMethodCode", source = "shippingMethod.shippingCode")
    KafkaOrderResponse mapToKafkaOrderResponse(OrderResponse orderResponse);

    @Mapping(target = "shippingCode", source = "shippingMethod.shippingCode")
    @Mapping(target = "status", source = "status")
    KafkaOrderShippingResponse mapToKafkaOrderShippingResponse(OrderEntity orderEntity);
}
