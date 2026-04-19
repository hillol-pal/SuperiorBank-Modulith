package com.superiorbank.account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountOpeningResponse(
        String accountId,
        String accountNumber,
        String customerId,
        BigDecimal balance,
        String currency,
        String status,
        Instant openedAt
) {
    static AccountOpeningResponse from(Account account) {
        return new AccountOpeningResponse(
                account.id().value().toString(),
                account.accountNumber(),
                account.customerId(),
                account.balance(),
                account.currency(),
                account.status().name(),
                account.openedAt()
        );
    }
}
