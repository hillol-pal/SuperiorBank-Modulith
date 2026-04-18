package com.superiorbank.fraud.internal;

import com.superiorbank.fraud.FraudScore;
import com.superiorbank.transaction.FraudCheckContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class RuleEngine {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal VELOCITY_THRESHOLD   = new BigDecimal("0.90"); // 90% of available balance

    public FraudScore evaluate(FraudCheckContext context) {
        // Rule 1: High-value international transfer
        if ("SWIFT".equals(context.transferType())
                && context.amount().compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            return FraudScore.high(0.75, "HIGH_VALUE_INTERNATIONAL");
        }

        // Rule 2: Account balance velocity — spending >90% of available balance at once
        if (context.availableBalance().compareTo(BigDecimal.ZERO) > 0) {
            var ratio = context.amount().divide(context.availableBalance(), 4, java.math.RoundingMode.HALF_UP);
            if (ratio.compareTo(VELOCITY_THRESHOLD) > 0) {
                return FraudScore.high(0.80, "BALANCE_VELOCITY_BREACH");
            }
        }

        // Rule 3: Self-transfer (same account source and target — potential money laundering signal)
        if (context.sourceAccountId().equals(context.targetAccountId())) {
            return FraudScore.critical(0.95, "SELF_TRANSFER");
        }

        return FraudScore.low();
    }
}
