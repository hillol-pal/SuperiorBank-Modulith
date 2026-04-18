package com.superiorbank.transaction.internal;

import com.superiorbank.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class TransactionLimitChecker {

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("50000.00");
    private static final BigDecimal SINGLE_TRANSACTION_LIMIT = new BigDecimal("25000.00");
    private static final int MAX_DAILY_TRANSACTIONS = 20;

    private final TransactionRepository transactionRepository;

    public void validate(Account account, BigDecimal amount) {
        if (amount.compareTo(SINGLE_TRANSACTION_LIMIT) > 0) {
            throw new TransactionLimitExceededException(
                "Single transaction limit exceeded: " + amount + " > " + SINGLE_TRANSACTION_LIMIT);
        }

        var since = Instant.now().minus(24, ChronoUnit.HOURS);
        long dailyCount = transactionRepository.countBySourceAccountIdAndCreatedAtAfter(
                account.id().value(), since);

        if (dailyCount >= MAX_DAILY_TRANSACTIONS) {
            throw new TransactionLimitExceededException(
                "Daily transaction count limit reached for account: " + account.id());
        }
    }
}
