package com.ecommerce.kientv84.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class KafkaObjectError {
    private String service;
    private String topic;
    private String messageError;
    private KafkaObjectError() {}
}
