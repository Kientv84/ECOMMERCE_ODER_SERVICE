package com.ecommerce.kientv84.services;


import com.fasterxml.jackson.core.type.TypeReference;

public interface RedisService {

    <T> void setValue(final String key, T data); // default TTL

    <T> void setValue(final String key, T data, int expireDurationSeconds);

    <T> T getValue(final String key, Class<T> valueType);

    <T> T getValue(final String key, TypeReference<T> typeReference);

    void deleteByKey(final String key);

    void deleteByKeys(final String... keys); // bulk delete
}

