package com.ecommerce.kientv84.dtos.requests.search.shippingMethod;

import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchModel;
import com.ecommerce.kientv84.dtos.requests.search.order.OrderSearchOption;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethodSearchRequest {
    private ShippingMethodSearchModel searchModel = new ShippingMethodSearchModel();
    private ShippingMethodSearchOption searchOption = new ShippingMethodSearchOption();

    public String hashKey() {
        return "option:" + searchModel.hashKey() + "|filter:" + searchOption.hashKey();
    }
}
