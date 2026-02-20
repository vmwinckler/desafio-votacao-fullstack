package com.cooperativa.assembleia.client;

import java.io.Serializable;
import java.util.Random;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CpfValidationClient {

    private final Random random = new Random();

    /**
     * Simulates an external API call to validate a CPF and check if it can vote.
     * Caches the result using Redis to optimize performance and prevent redundant requests.
     * 
     * @param cpf The CPF to validate
     * @return CpfValidationResponse containing the status
     * @throws ResponseStatusException 404 NOT_FOUND if CPF is generated as invalid
     */
    // @Cacheable(value = "cpfValidation", key = "#cpf")
    public CpfValidationResponse validateCpf(String cpf) {
        log.info("Simulating external API call for CPF validation: {}", cpf);
        
        // Simulate network latency
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 20% chance to be invalid (404)
        if (random.nextInt(100) < 20) {
            log.warn("CPF {} is invalid (Simulated 404)", cpf);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF Inválido");
        }

        // 50% chance out of the remaining to be ABLE or UNABLE
        String status = random.nextBoolean() ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE";
        
        log.info("CPF {} validated: {}", cpf, status);
        return new CpfValidationResponse(status);
    }

    public static class CpfValidationResponse implements Serializable {
        private String status;
        
        // Needed for Jackson/Redis deserialization
        public CpfValidationResponse() {}

        public CpfValidationResponse(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
