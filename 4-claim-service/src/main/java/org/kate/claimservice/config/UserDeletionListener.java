package org.kate.claimservice.config;


import org.kate.claimservice.config.RabbitConfig;
import org.kate.claimservice.repository.ClaimRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDeletionListener {

    @Autowired
    private ClaimRepository claimRepository;

    // This method runs automatically when a message hits the queue
    @RabbitListener(queues = RabbitConfig.USER_DELETE_QUEUE)
    public void handleUserDeleted(String userIdentificationNumber) {
        System.out.println("Cleaning up claims for user: " + userIdentificationNumber);

        // Triggers the @Modifying @Query you wrote in your repository
        claimRepository.deleteByUserIdentificationNumber(userIdentificationNumber);
    }
}
