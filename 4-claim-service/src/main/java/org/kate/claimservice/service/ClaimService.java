package org.kate.claimservice.service;

import org.kate.claimservice.model.ClaimCreatedEvent;
import org.kate.claimservice.config.RabbitConfig;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.model.Policy;
import org.kate.claimservice.repository.ClaimRepository;
import org.kate.claimservice.repository.PolicyRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Claim postClaim(ClaimDTO claimDto) {
        // 1. Verify the policy exists in the database
        Policy policy = policyRepository.findByPolicyNumber(claimDto.getPolicyNumber())
                .orElseThrow(() -> new RuntimeException("Policy not found for number: " + claimDto.getPolicyNumber()));

        // 2. Map the DTO data to a new Claim Entity
        Claim claim = new Claim();
        claim.setAmount(claimDto.getAmount());
        claim.setDescription(claimDto.getDescription());
        claim.setPolicyNumber(claimDto.getPolicyNumber());
        claim.setStatus("PENDING"); // Default status for new claims

        // 3. Save and return the claim
        return claimRepository.save(claim);
    }

    public Claim submitClaim(Claim claim) {
            // 1. Save to DB
            Claim savedClaim = claimRepository.save(claim);

            // 2. Create the Event
            ClaimCreatedEvent event = new ClaimCreatedEvent(
                    savedClaim.getId(),
                    savedClaim.getUserId(),
                    savedClaim.getPolicyNumber()
            );

            // 3. Send to RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.ROUTING_KEY,
                    event
            );

            return savedClaim;
        }
    }
