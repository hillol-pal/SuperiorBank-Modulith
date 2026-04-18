package com.superiorbank.account;

import com.superiorbank.account.internal.AccountJpaEntity;
import com.superiorbank.account.internal.AccountMapper;
import com.superiorbank.account.internal.AccountRepository;
import com.superiorbank.account.internal.AccountValidator;
import com.superiorbank.account.internal.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Public API for the Account module.
 * This is the ONLY entry point other modules may call.
 * All classes in account/internal are strictly off-limits to other modules.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountManagementService {

    private final AccountRepository accountRepository;
    private final AccountValidator accountValidator;
    private final BalanceCalculator balanceCalculator;
    private final ApplicationEventPublisher events;

    @Transactional(readOnly = true)
    public Optional<Account> findById(AccountId accountId) {
        return accountRepository.findById(accountId.value())
                .map(AccountMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAvailableBalance(AccountId accountId) {
        return accountRepository.findById(accountId.value())
                .map(balanceCalculator::calculateAvailable)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public Account openAccount(String customerId, String currency) {
        accountValidator.validateNewAccount(customerId, currency);

        var entity = AccountJpaEntity.open(customerId, currency);
        var saved = accountRepository.save(entity);
        var domain = AccountMapper.toDomain(saved);

        events.publishEvent(new AccountOpenedEvent(domain.id(), customerId, currency));

        log.info("Opened account {} for customer {}", domain.accountNumber(), customerId);
        return domain;
    }

    @Transactional
    public void debit(AccountId accountId, BigDecimal amount, String reference) {
        var entity = accountRepository.findByIdForUpdate(accountId.value())
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        accountValidator.validateDebit(entity, amount);
        entity.debit(amount);

        accountRepository.save(entity);
        log.info("Debited {} {} from account {} (ref: {})", amount, entity.getCurrency(), accountId, reference);
    }

    @Transactional
    public void credit(AccountId accountId, BigDecimal amount, String reference) {
        var entity = accountRepository.findByIdForUpdate(accountId.value())
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        entity.credit(amount);
        accountRepository.save(entity);
        log.info("Credited {} {} to account {} (ref: {})", amount, entity.getCurrency(), accountId, reference);
    }
}
