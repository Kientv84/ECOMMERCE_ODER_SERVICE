package com.ecommerce.kientv84.services;

import com.ecommerce.kientv84.dtos.requests.ShippingMethodRequest;
import com.ecommerce.kientv84.dtos.requests.ShippingMethodUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.ShippingMethodResponse;

import java.util.List;
import java.util.UUID;

public interface ShippingMethodService {
    List<ShippingMethodResponse> getAllShippingMethod();

    ShippingMethodResponse createShippingMethod(ShippingMethodRequest request);

    ShippingMethodResponse updateShippingMethod(UUID id, ShippingMethodUpdateRequest updateData);

    ShippingMethodResponse getShippingMethodById(UUID id);

    String deleteShippingMethod(List<UUID> ids);
}
