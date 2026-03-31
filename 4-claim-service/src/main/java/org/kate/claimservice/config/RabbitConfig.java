package org.kate.claimservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "claim_exchange";
    public static final String QUEUE = "notification_queue";
    public static final String ROUTING_KEY = "claim_routing_key";

    public static final String USER_DELETE_EXCHANGE = "user.deletion.exchange";
    public static final String USER_DELETE_QUEUE = "user.deletion.queue";
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
@Bean
public TopicExchange userDeleteExchange() {
    return new TopicExchange(USER_DELETE_EXCHANGE);
}

@Bean
public Queue userDeleteQueue() {
    return new Queue(RabbitConfig.USER_DELETE_QUEUE, true);
}

@Bean
public Binding userDeleteBinding(Queue userDeleteQueue, TopicExchange userDeleteExchange) {
    // Using an empty string "" as the routing key so it catches all deletion messages
    return BindingBuilder.bind(userDeleteQueue).to(userDeleteExchange).with("");
}
@Bean
public Jackson2JsonMessageConverter converter() {
    return new Jackson2JsonMessageConverter();
}}