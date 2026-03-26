package org.kate._usersservice.UserService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CLAIM-SERVICE") // This matches the name in Eureka
public interface ClaimClient {

    @DeleteMapping("/api/claims/user/{idNumber}")
    void deleteClaimsByUserId(@PathVariable("idNumber") String idNumber);
}
