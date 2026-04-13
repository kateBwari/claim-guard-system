package org.kate.frauddetectionservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FRAUD_QUEUE = "fraud_check_queue";

    @Bean
    public Queue queue() {
        return new Queue(FRAUD_QUEUE);
    }

    // This converts the JSON from RabbitMQ back into your Java ClaimRequest object
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}