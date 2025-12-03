package com.ecommerce.kientv84.dtos.requests.search.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSearchRequest {
    private OrderSearchModel orderSearchModel = new OrderSearchModel();
    private OrderSearchOption orderSearchOption = new OrderSearchOption();

     public String hashKey() {
         return "option:" + orderSearchModel.hashKey() + "|filter:" + orderSearchOption.hashKey();
    }
}
