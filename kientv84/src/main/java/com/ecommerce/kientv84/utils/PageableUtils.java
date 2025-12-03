package com.ecommerce.kientv84.utils;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PageableUtils {

    public static Sort buildSort(String sortStr, List<String> allowedFields, String defaultField, Sort.Direction defaultDirection) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sortStr != null && !sortStr.isEmpty()) {
            String[] parts = sortStr.split(",");
            for (int i = 0; i < parts.length; i += 2) {
                String field = parts[i].trim();
                if (!allowedFields.contains(field)) continue;

                Sort.Direction direction = Sort.Direction.ASC;
                if (i + 1 < parts.length && parts[i + 1].trim().equalsIgnoreCase("desc")) {
                    direction = Sort.Direction.DESC;
                }
                orders.add(new Sort.Order(direction, field));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new Sort.Order(defaultDirection, defaultField));
        }

        return Sort.by(orders);
    }

    /**
     * Tạo PageRequest từ page, size và sortStr
     */
    public static PageRequest buildPageRequest(
            Integer page, Integer size,
            String sortStr, List<String> allowedFields,
            String defaultField, Sort.Direction defaultDirection
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Sort sort = buildSort(sortStr, allowedFields, defaultField, defaultDirection);
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
