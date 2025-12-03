package com.ecommerce.kientv84.dtos.requests.search.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSearchModel {
    private String q;
    private String status;
    private String orderCode;

    public String hashKey() {
        return ( q + "-" + status + "-" + orderCode);
    }
}
