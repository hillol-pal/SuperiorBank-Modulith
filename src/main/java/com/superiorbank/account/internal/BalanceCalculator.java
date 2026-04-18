package com.superiorbank.account.internal;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceCalculator {

    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("500.00");

    public BigDecimal calculateAvailable(AccountJpaEntity account) {
        return account.getBalance().add(OVERDRAFT_LIMIT);
    }
}
