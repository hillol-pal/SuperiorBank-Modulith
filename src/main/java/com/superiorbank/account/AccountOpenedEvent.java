package com.superiorbank.account;

import java.time.Instant;

/**
 * Published when a new account is successfully opened.
 * Consumed by the loan module to initialise eligibility profiles.
 */
public record AccountOpenedEvent(
        AccountId accountId,
        String customerId,
        String currency,
        Instant occurredAt
) {
    public AccountOpenedEvent(AccountId accountId, String customerId, String currency) {
        this(accountId, customerId, currency, Instant.now());
    }
}
