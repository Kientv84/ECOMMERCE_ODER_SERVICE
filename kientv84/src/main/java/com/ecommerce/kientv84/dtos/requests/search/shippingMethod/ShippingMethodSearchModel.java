package com.ecommerce.kientv84.dtos.requests.search.shippingMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethodSearchModel {
    private String q;
    private String status;
    private String shippingMethodCode;

    public String hashKey() {
        return ( q + "-" + status + "-" + shippingMethodCode);
    }
}
