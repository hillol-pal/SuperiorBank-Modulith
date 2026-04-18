package com.superiorbank.transaction;

import com.superiorbank.account.AccountId;
import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published when a transaction is blocked by the fraud gateway.
 * Consumed by the notification module (send fraud alert) and fraud module (log case).
 */
@Externalized("nexabank.transaction.blocked::#{#this.transactionId}")
public record TransactionBlockedEvent(
        TransactionId transactionId,
        AccountId sourceAccountId,
        String sourceCustomerId,
        BigDecimal amount,
        String currency,
        double fraudScore,
        String ruleTriggered,
        Instant occurredAt
) {
    public TransactionBlockedEvent(
            TransactionId transactionId,
            AccountId sourceAccountId,
            String sourceCustomerId,
            BigDecimal amount,
            String currency,
            FraudVerdict verdict) {
        this(transactionId, sourceAccountId, sourceCustomerId,
                amount, currency, verdict.score(), verdict.ruleTriggered(), Instant.now());
    }
}
