package com.ecommerce.kientv84.messagsing.error;

import com.ecommerce.kientv84.utils.KafkaObjectError;

public interface KafkaErrorHandle {
    void handleError(KafkaObjectError error, Exception exception);
}
