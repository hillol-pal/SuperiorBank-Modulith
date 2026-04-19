package com.superiorbank.account;

import java.time.Instant;

/**
 * Published when a new account is successfully opened.
 * Consumed by the loan module (eligibility profile) and notification module (welcome email).
 */
public record AccountOpenedEvent(
        AccountId accountId,
        String customerId,
        String currency,
        String customerEmail,
        Instant occurredAt
) {
    public AccountOpenedEvent(AccountId accountId, String customerId, String currency, String customerEmail) {
        this(accountId, customerId, currency, customerEmail, Instant.now());
    }
}
