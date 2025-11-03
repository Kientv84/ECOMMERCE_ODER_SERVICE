package com.ecommerce.kientv84.messaging.error;

import com.ecommerce.kientv84.utils.KafkaObjectError;

public interface KafkaErrorHandle {
    void handleError(KafkaObjectError error, Exception exception);
}
