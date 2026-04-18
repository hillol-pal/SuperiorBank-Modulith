package com.superiorbank.account.internal;

import java.math.BigDecimal;
import java.util.UUID;

class InsufficientFundsException extends RuntimeException {

    InsufficientFundsException(UUID accountId, BigDecimal requested, BigDecimal available) {
        super(String.format(
            "Insufficient funds in account %s: requested=%s, available=%s",
            accountId, requested, available));
    }
}
