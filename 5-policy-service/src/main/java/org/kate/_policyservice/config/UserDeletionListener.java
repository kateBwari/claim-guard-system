package org.kate._policyservice.config;

import org.kate._policyservice.repository.PolicyRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDeletionListener {

    @Autowired
    private PolicyRepository policyRepository;

    // This method runs automatically when a message hits the queue
    @RabbitListener(queues ="policy.user.deletion.queue")
    @Transactional
    public void handleUserDeleted(String userIdentificationNumber) {
        System.out.println("Message Received: Deleting policies for User ID: " + userIdentificationNumber);
        try {
            // Triggers the @Modifying @Query you wrote in your repository
            policyRepository.deleteByUserIdentificationNumber(userIdentificationNumber);
            System.out.println("Success: All policies for " + userIdentificationNumber + " have been removed.");
        } catch (Exception e) {
            System.err.println("Error deleting policies: " + e.getMessage());
            // The transaction will roll back if an error occurs
        }
    }}

