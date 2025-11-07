//package com.ecommerce.kientv84.config;
//
//import com.ecommerce.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
//import com.ecommerce.kientv84.dtos.responses.kafka.KafkaShipmentStatusUpdated;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConsumerConfig {
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.payment.group}")
//    private String paymentGroup;
//
//    @Value("${spring.kafka.shipping.group}")
//    private String shippingGroup;
//
//    @Value("${spring.kafka.consumer.auto-offset-reset}")
//    private String autoOffsetReset;
//
//    // === PAYMENT CONSUMER ===
//    @Bean
//    public ConsumerFactory<String, KafkaPaymentResponse> paymentConsumerFactory() {
//        JsonDeserializer<KafkaPaymentResponse> jsonDeserializer =
//                new JsonDeserializer<>(KafkaPaymentResponse.class);
//        jsonDeserializer.addTrustedPackages("*");
//        jsonDeserializer.ignoreTypeHeaders();
//        jsonDeserializer.setUseTypeMapperForKey(false);
//
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, paymentGroup);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        // ⚡ Dùng ErrorHandlingDeserializer để xử lý lỗi deserialization
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
//        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
//        // ⚡ Cấu hình default type khi không có header type
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaPaymentResponse.class.getName());
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, KafkaPaymentResponse> paymentKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, KafkaPaymentResponse> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(paymentConsumerFactory());
//        return factory;
//    }
//
//    // === SHIPMENT CONSUMER ===
//    @Bean
//    public ConsumerFactory<String, KafkaShipmentStatusUpdated> shipmentConsumerFactory() {
//        JsonDeserializer<KafkaShipmentStatusUpdated> jsonDeserializer =
//                new JsonDeserializer<>(KafkaShipmentStatusUpdated.class);
//        jsonDeserializer.addTrustedPackages("*");
//        jsonDeserializer.ignoreTypeHeaders();
//        jsonDeserializer.setUseTypeMapperForKey(false);
//
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, shippingGroup);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
//        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaShipmentStatusUpdated.class.getName());
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, KafkaShipmentStatusUpdated> shipmentKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, KafkaShipmentStatusUpdated> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(shipmentConsumerFactory());
//        return factory;
//    }
//}