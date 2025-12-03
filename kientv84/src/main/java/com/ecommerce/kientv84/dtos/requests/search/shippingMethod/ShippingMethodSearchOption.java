package com.ecommerce.kientv84.dtos.requests.search.shippingMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethodSearchOption {
    private Integer page = 0;
    private Integer size = 10;
    private String sort = "createDate,desc";

    public String hashKey() {
        return page + '-' + size + '-' + sort;
    }
}
