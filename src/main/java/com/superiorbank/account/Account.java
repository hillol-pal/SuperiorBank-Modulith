package com.superiorbank.account;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Public read-only domain view of an Account.
 * Other modules receive this type but cannot modify account state directly —
 * all mutations go through AccountManagementService.
 */
public record Account(
        AccountId id,
        String accountNumber,
        String customerId,
        BigDecimal balance,
        String currency,
        AccountStatus status,
        Instant openedAt
) {
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }
}
