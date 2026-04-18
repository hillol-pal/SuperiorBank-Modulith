package com.superiorbank.fraud.internal;

import com.superiorbank.transaction.TransactionBlockedEvent;
import com.superiorbank.transaction.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to transaction events to keep the fraud module's state up to date.
 *
 * @ApplicationModuleListener guarantees:
 *  - Runs in a NEW transaction, AFTER the originating transaction commits.
 *  - If the source transaction rolls back, this listener never fires.
 *  - If this listener fails, Spring Modulith re-delivers on next restart.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class TransactionFraudListener {

    private final BehavioralAnalyzer behavioralAnalyzer;
    private final FraudCaseRepository fraudCaseRepository;

    @ApplicationModuleListener
    void onTransactionCompleted(TransactionCompletedEvent event) {
        behavioralAnalyzer.recordCompletedTransaction(
                event.sourceCustomerId(),
                event.amount(),
                event.occurredAt());
        log.debug("Updated behavioral profile for customer: {}", event.sourceCustomerId());
    }

    @ApplicationModuleListener
    void onTransactionBlocked(TransactionBlockedEvent event) {
        var fraudCase = FraudCaseRecord.of(
                event.transactionId().value(),
                event.sourceCustomerId(),
                event.fraudScore(),
                "HIGH",
                event.ruleTriggered());

        fraudCaseRepository.save(fraudCase);
        log.info("Fraud case logged for transaction: {}", event.transactionId());
    }
}
