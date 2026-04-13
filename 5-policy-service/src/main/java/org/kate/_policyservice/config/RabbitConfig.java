package org.kate._policyservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- User Deletion Logic (Broadcast from User Service) ---
    public static final String USER_DELETE_EXCHANGE = "user.deletion.exchange";
    public static final String POLICY_USER_DELETE_QUEUE = "policy.user.deletion.queue";

    // 1. Define the Fanout Exchange
    @Bean
    public FanoutExchange userDeleteExchange() {
        return new FanoutExchange(USER_DELETE_EXCHANGE);
    }

    // 2. Define the Unique Queue for Policy Service
    @Bean
    public Queue policyUserDeleteQueue() {
        return new Queue(POLICY_USER_DELETE_QUEUE, true);
    }

    // 3. Bind them together
    @Bean
    public Binding policyDeleteBinding(Queue policyUserDeleteQueue, FanoutExchange userDeleteExchange) {
        return BindingBuilder.bind(policyUserDeleteQueue).to(userDeleteExchange);
    }

    // 4. JSON Converter for the User ID/Message
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();

}}