package com.superiorbank.fraud.internal;

import com.superiorbank.fraud.FraudScore;
import com.superiorbank.transaction.FraudCheckContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class BehavioralAnalyzer {

    private final CustomerBehaviorRepository behaviorRepository;

    public FraudScore analyze(FraudCheckContext context) {
        return behaviorRepository.findByCustomerId(context.customerId())
                .map(profile -> scoreAgainstProfile(context, profile))
                .orElse(FraudScore.low()); // new customer, no history = low score
    }

    void recordCompletedTransaction(String customerId, BigDecimal amount, Instant occurredAt) {
        var profile = behaviorRepository.findByCustomerId(customerId)
                .orElse(CustomerBehaviorRecord.newProfile(customerId));

        profile.recordTransaction(amount, occurredAt);
        behaviorRepository.save(profile);
    }

    private FraudScore scoreAgainstProfile(FraudCheckContext context, CustomerBehaviorRecord profile) {
        if (profile.getTransactionCount() < 3) {
            return FraudScore.low();
        }

        // Amount is more than 3x the customer's average — behavioural anomaly
        var avg = profile.getAvgTransactionAmt();
        if (avg.compareTo(BigDecimal.ZERO) > 0) {
            var ratio = context.amount().divide(avg, 4, RoundingMode.HALF_UP);
            if (ratio.compareTo(new BigDecimal("3.0")) > 0) {
                double score = Math.min(0.60 + (ratio.doubleValue() - 3.0) * 0.05, 0.90);
                return FraudScore.medium(score, "AMOUNT_ANOMALY");
            }
        }

        return FraudScore.low();
    }
}
