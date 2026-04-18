package com.superiorbank.transaction;

import com.superiorbank.account.Account;

import java.math.BigDecimal;

/**
 * Input context for fraud assessment. Uses only primitive/standard types
 * to avoid the transaction module importing fraud module types (which would create a cycle).
 */
public record FraudCheckContext(
        String sourceAccountId,
        String targetAccountId,
        String customerId,
        BigDecimal amount,
        String currency,
        String transferType,
        BigDecimal availableBalance
) {
    public static FraudCheckContext from(TransferRequest request, Account sourceAccount, BigDecimal availableBalance) {
        return new FraudCheckContext(
                sourceAccount.id().toString(),
                request.targetAccountId().toString(),
                sourceAccount.customerId(),
                request.amount(),
                request.currency(),
                request.transferType(),
                availableBalance
        );
    }
}
