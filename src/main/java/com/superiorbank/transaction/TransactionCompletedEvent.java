package com.superiorbank.transaction;

import com.superiorbank.account.AccountId;
import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published when a fund transfer completes successfully.
 *
 * @Externalized maps this event to a Kafka topic when the kafka-events profile is active.
 * The routing key is the transactionId — guarantees ordered delivery per transaction.
 * Listener code (in notification, fraud modules) does NOT change when externalization is toggled.
 */
@Externalized("nexabank.transaction.completed::#{#this.transactionId}")
public record TransactionCompletedEvent(
        TransactionId transactionId,
        AccountId sourceAccountId,
        AccountId targetAccountId,
        BigDecimal amount,
        String currency,
        String sourceCustomerId,
        String targetCustomerId,
        Instant occurredAt
) {
    public TransactionCompletedEvent(
            TransactionId transactionId,
            AccountId sourceAccountId,
            AccountId targetAccountId,
            BigDecimal amount,
            String currency,
            String sourceCustomerId,
            String targetCustomerId) {
        this(transactionId, sourceAccountId, targetAccountId,
                amount, currency, sourceCustomerId, targetCustomerId, Instant.now());
    }
}
