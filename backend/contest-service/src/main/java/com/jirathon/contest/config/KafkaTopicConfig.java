package com.jirathon.contest.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Ensures the payment.success topic exists with enough partitions
 * so every contest-service instance gets at least one partition assigned.
 *
 * Spring Kafka's NewTopic bean is idempotent — if the topic already exists
 * with >= the requested partition count it is left unchanged.
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentSuccessTopic() {
        return TopicBuilder.name("payment.success")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
