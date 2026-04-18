package com.superiorbank.loan.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
@Slf4j
public class CreditAssessmentEngine {

    private final Random rng = new Random();

    public int assess(String customerId, BigDecimal requestedAmount) {
        // Stub: in production this calls a credit bureau API (Experian, Equifax, etc.)
        // Returns a credit score 300–850
        int baseScore = 600 + rng.nextInt(250);
        log.debug("Credit score for customer {}: {}", customerId, baseScore);
        return baseScore;
    }
}
