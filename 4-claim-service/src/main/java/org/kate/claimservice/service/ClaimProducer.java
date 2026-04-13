package org.kate.claimservice.service;

import org.kate.claimservice.dtos.ClaimDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // These names must match your RabbitMQ Management Console setup
    private static final String EXCHANGE = "claim.exchange";
    private static final String FRAUD_ROUTING_KEY = "claim.fraud.check";
    private static final String NOTIFY_ROUTING_KEY = "claim.notification.send";

    public void sendClaimToAll(ClaimDTO claimDto) {
        // 1. Send to Fraud Detection Service
        rabbitTemplate.convertAndSend(EXCHANGE, FRAUD_ROUTING_KEY, claimDto);

        // 2. Send to Notification Service
        rabbitTemplate.convertAndSend(EXCHANGE, NOTIFY_ROUTING_KEY, claimDto);

        System.out.println("Sent Claim " + claimDto.getClaimNumber() + " to Fraud and Notification.");
    }
}