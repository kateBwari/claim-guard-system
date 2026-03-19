package org.kate.notificationservice;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationConsumer {

    @RabbitListener(queues = "notification_queue")
    public void listen(Map<String, Object> message) { // Change String to Map
        System.out.println("Received message: " + message);
    }
}
