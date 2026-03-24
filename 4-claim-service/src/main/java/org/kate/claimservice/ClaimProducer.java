package org.kate.claimservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToQueue(Object claimData) {
        // "notification_queue" is the name of the box we are dropping it into
        rabbitTemplate.convertAndSend("notification_queue", claimData);
        System.out.println("Success: Claim data sent to RabbitMQ!");
    }
}
