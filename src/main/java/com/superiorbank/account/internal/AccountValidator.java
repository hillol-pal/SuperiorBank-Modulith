package com.superiorbank.account.internal;

import com.superiorbank.account.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class AccountValidator {

    private static final BigDecimal MIN_BALANCE = new BigDecimal("-500.00"); // £500 overdraft limit

    public void validateDebit(AccountJpaEntity account, BigDecimal amount) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "Cannot debit account " + account.getId() + " with status " + account.getStatus());
        }
        BigDecimal projectedBalance = account.getBalance().subtract(amount);
        if (projectedBalance.compareTo(MIN_BALANCE) < 0) {
            throw new InsufficientFundsException(account.getId(), amount, account.getBalance());
        }
    }

    public void validateNewAccount(String customerId, String currency) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID must not be blank");
        }
        if (!isSupportedCurrency(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }

    private boolean isSupportedCurrency(String currency) {
        return "GBP".equals(currency) || "EUR".equals(currency) || "USD".equals(currency) || "INR".equals(currency);
    }
}
