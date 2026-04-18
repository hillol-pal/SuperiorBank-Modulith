package com.superiorbank.transaction;

import com.superiorbank.account.AccountManagementService;
import com.superiorbank.account.AccountNotFoundException;
import com.superiorbank.transaction.internal.TransactionJpaEntity;
import com.superiorbank.transaction.internal.TransactionLimitChecker;
import com.superiorbank.transaction.internal.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core business service for fund transfers.
 *
 * Design highlights:
 *  1. Single @Transactional boundary — debit, credit, and event publication are atomic.
 *     If the DB commit fails, no events are delivered. No phantom notifications.
 *  2. FraudGateway is a port this module owns; fraud module provides the adapter.
 *     This breaks the circular dependency that would arise from calling FraudAssessmentService directly.
 *  3. Post-transaction concerns (notifications, fraud model updates) are entirely
 *     event-driven — TransactionService has zero knowledge of those modules.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final AccountManagementService accountService;
    private final FraudGateway fraudGateway;
    private final TransactionRepository transactionRepository;
    private final TransactionLimitChecker limitChecker;
    private final ApplicationEventPublisher events;

    @Transactional
    public TransactionResult processTransfer(TransferRequest request) {
        log.info("Processing {} transfer: {} → {} for {} {}",
                request.transferType(),
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                request.currency());

        var sourceAccount = accountService.findById(request.sourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.sourceAccountId()));

        var targetAccount = accountService.findById(request.targetAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.targetAccountId()));

        if (!sourceAccount.isActive()) {
            throw new IllegalStateException("Source account is not active: " + request.sourceAccountId());
        }

        limitChecker.validate(sourceAccount, request.amount());

        var availableBalance = accountService.getAvailableBalance(request.sourceAccountId());
        var fraudContext = FraudCheckContext.from(request, sourceAccount, availableBalance);
        var verdict = fraudGateway.verify(fraudContext);

        var transactionId = TransactionId.generate();

        if (verdict.isHighRisk()) {
            log.warn("Transfer BLOCKED by fraud gateway. Score={}, Rule={}, Account={}",
                    verdict.score(), verdict.ruleTriggered(), request.sourceAccountId());

            transactionRepository.save(TransactionJpaEntity.blocked(transactionId, request, verdict.score()));

            events.publishEvent(new TransactionBlockedEvent(
                    transactionId,
                    request.sourceAccountId(),
                    sourceAccount.customerId(),
                    request.amount(),
                    request.currency(),
                    verdict));

            return TransactionResult.blocked(transactionId, verdict.ruleTriggered());
        }

        // Both operations inside the same @Transactional — ACID guaranteed.
        // No sagas. No compensating transactions. No eventual consistency.
        accountService.debit(request.sourceAccountId(), request.amount(), transactionId.toString());
        accountService.credit(request.targetAccountId(), request.amount(), transactionId.toString());

        transactionRepository.save(TransactionJpaEntity.completed(transactionId, request, verdict.score()));

        events.publishEvent(new TransactionCompletedEvent(
                transactionId,
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                request.currency(),
                sourceAccount.customerId(),
                targetAccount.customerId()));

        log.info("Transfer COMPLETED. TransactionId={}", transactionId);
        return TransactionResult.success(transactionId);
    }
}
