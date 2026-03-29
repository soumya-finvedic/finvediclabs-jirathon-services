package com.jirathon.contest.config;

import com.jirathon.contest.event.PaymentSuccessEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:29092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:contest-service}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, PaymentSuccessEvent> consumerFactory() {
        JsonDeserializer<PaymentSuccessEvent> jsonDeserializer =
                new JsonDeserializer<>(PaymentSuccessEvent.class, false);
        jsonDeserializer.addTrustedPackages("com.jirathon.payment.event", "com.jirathon.contest.event");

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // ── Prevents consumer group rebalance during slow MongoDB saves ───────
        // If processPaymentSuccess() takes longer than max.poll.interval.ms,
        // Kafka kicks the consumer out and reassigns partitions — causing
        // the message to be redelivered to another instance.
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300_000);  // 5 min
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30_000);     // 30 sec
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10_000);  // 10 sec

        // ── Reduces stale-consumer window on restart ──────────────────────────
        // After a restart, old session expires faster so the new instance
        // gets the partition assigned sooner instead of receiving partitions=[].
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
