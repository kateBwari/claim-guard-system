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
    public ClaimService(ClaimRepository repository) {
        this.claimRepository = repository;
    }

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
        claim.setBenefitType(claimDto.getBenefitType());
        claim.setDescription(claimDto.getDescription());
        claim.setPolicyNumber(claimDto.getPolicyNumber());
        claim.setUserIdentificationNumber(claimDto.getUserIdentificationNumber());
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
                    savedClaim.getUserIdentificationNumber(),
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
        public ClaimDTO deleteClaim(Long id) {
        // 1. Find the claim or throw an error if it doesn't exist
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));

        // 2. Map the Entity to a DTO (assuming you have a mapper or manual conversion)
        ClaimDTO claimDto = new ClaimDTO();
        claimDto.setId(claim.getId());
        // ... map other fields like claimNumber, status, etc.

        // 3. Delete from the database
        claimRepository.delete(claim);

        // 4. Return the DTO
        return claimDto;
    }
}
