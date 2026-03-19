package org.kate.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue notificationQueue() {
        return new Queue("notification_queue", true);
    }
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public TopicExchange claimExchange() {
        return new TopicExchange("claim-exchange");
    }
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange claimExchange) {
        // Use 'notificationQueue' here to match the parameter name above
        return BindingBuilder.bind(notificationQueue)
                .to(claimExchange)
                .with("claim.routing.key");
    }
}