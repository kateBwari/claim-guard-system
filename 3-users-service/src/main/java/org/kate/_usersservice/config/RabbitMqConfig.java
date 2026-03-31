package org.kate._usersservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE = "user.deletion.exchange";
    public static final String QUEUE = "user.deletion.queue";

    @Bean
    public TopicExchange deletionExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue deletionQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding binding(Queue deletionQueue, TopicExchange deletionExchange) {
        return BindingBuilder.bind(deletionQueue).to(deletionExchange).with("");
    }
}

