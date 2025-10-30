package com.ecommerce.kientv84.services.Impls;

import com.ecommerce.kientv84.services.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaServiceImpl<K, V> implements KafkaService {
    private final KafkaTemplate<K, V> kvKafkaTemplate;

    @Override
    public void send(String topic, Object value) {
        this.send(topic, null, value);
    }

    @Override
    public void send(String topic, Object key, Object value) {
        try {
            kvKafkaTemplate.send(
                    MessageBuilder.withPayload(value)
                            .setHeader(KafkaHeaders.TOPIC, topic)
                            .setHeader(KafkaHeaders.KEY, key)
                            .build()
            );
        } catch (SerializationException exception) {
            log.error("SerializationException" + exception.getMessage(), exception);
        }
    }
}
