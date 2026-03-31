package org.kate.claimservice.service;

import com.google.common.io.Files;
import org.kate.claimservice.ClaimProducer;
import org.kate.claimservice.model.ClaimCreatedEvent;
import org.kate.claimservice.config.RabbitConfig;
import org.kate.claimservice.model.Claim;
import org.kate.claimservice.model.ClaimDTO;
import org.kate.claimservice.model.Policy;
import org.kate.claimservice.repository.ClaimRepository;
import org.kate.claimservice.repository.PolicyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimService {
    @Autowired
    private ClaimRepository claimRepository;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ClaimProducer claimProducer;
    @Autowired
    private ModelMapper modelMapper;


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
    @Transactional
    public void deleteByUserIdentificationNumber(String userIdentificationNumber) {
        claimRepository.deleteByUserIdentificationNumber(userIdentificationNumber);
    }
    public Claim createNewClaim(ClaimDTO claimDTO, String subject) {
        // 1. Check if a claim with the same policy and description already exists
        if (claimRepository.existsByPolicyNumberAndDescriptionAndUserIdentificationNumber(
                claimDTO.getPolicyNumber(), claimDTO.getDescription(), subject)) {
            // You can throw a custom exception here, e.g., ClaimAlreadyExistsException
            throw new RuntimeException("Claim already exists for this policy and description");
        }

        // 2. Map DTO to Entity
        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber()); // Method moved to Service
        claim.setDescription(claimDTO.getDescription());
        claim.setBenefitType(claimDTO.getBenefitType());
        claim.setPolicyNumber(claimDTO.getPolicyNumber());
        claim.setStatus("PENDING");

        // Set the user identifier from the token (the 'subject' passed from controller)
        claim.setUserIdentificationNumber(claimDTO.getUserIdentificationNumber());

        // 3. Save to Database
        Claim savedClaim = claimRepository.save(claim);

        // 4. Produce to Queue (if using Messaging)
        if (claimProducer != null) {
            claimProducer.sendToQueue(savedClaim);
        }

        return savedClaim;
    }

    // Move the generation logic here as a private helper method
    private String generateClaimNumber() {
        String prefix = "CLM";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        int randomNum = (int) (Math.random() * 900) + 100;
        return prefix + "-" + timestamp + "-" + randomNum;
    }

    public List<Claim> getClaimsByIdNumber(String idNumber) {
        // We use the injected claimRepository to fetch the data
        return claimRepository.findByUserIdentificationNumber(idNumber);
    }

    public List<ClaimDTO> getAllClaims() {
            // 1. Fetch all claim entities from the database
            List<Claim> claims = claimRepository.findAll();

            // 2. Convert the list of entities to a list of DTOs
            return claims.stream()
                    .map(claim -> modelMapper.map(claim, ClaimDTO.class))
                    .collect(Collectors.toList());
        }
    }


