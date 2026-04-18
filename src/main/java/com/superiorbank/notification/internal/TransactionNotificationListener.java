package com.superiorbank.notification.internal;

import com.superiorbank.transaction.TransactionBlockedEvent;
import com.superiorbank.transaction.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to transaction domain events and dispatches customer notifications.
 *
 * This class knows about TransactionCompletedEvent (the published contract from the
 * transaction module) but knows NOTHING about TransactionService, TransactionRepository,
 * or any other transaction module internals. The boundary is clean.
 *
 * @ApplicationModuleListener guarantees this runs AFTER the originating transaction
 * commits — so customers are never notified for a transaction that rolled back.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class TransactionNotificationListener {

    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;
    private final PushNotificationSender pushSender;
    private final NotificationPreferenceRepository preferenceRepository;

    @ApplicationModuleListener
    void onTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Dispatching transaction confirmation for customer: {}", event.sourceCustomerId());

        preferenceRepository.findByCustomerId(event.sourceCustomerId())
                .ifPresentOrElse(
                        prefs -> dispatchConfirmation(event, prefs),
                        () -> log.warn("No notification preferences for customer: {}", event.sourceCustomerId()));
    }

    @ApplicationModuleListener
    void onTransactionBlocked(TransactionBlockedEvent event) {
        log.warn("Dispatching fraud alert for customer: {}", event.sourceCustomerId());

        preferenceRepository.findByCustomerId(event.sourceCustomerId())
                .ifPresent(prefs -> {
                    // Fraud alerts are always sent regardless of opt-out preferences
                    if (prefs.getEmailAddress() != null) {
                        emailSender.sendFraudAlert(prefs.getEmailAddress(), event.transactionId().toString());
                    }
                    if (prefs.getPhoneNumber() != null) {
                        smsSender.sendFraudAlert(prefs.getPhoneNumber(), event.transactionId().toString());
                    }
                });
    }

    private void dispatchConfirmation(TransactionCompletedEvent event, NotificationPreferenceRecord prefs) {
        if (prefs.isEmailEnabled() && prefs.getEmailAddress() != null) {
            emailSender.send(EmailContent.of(
                    prefs.getEmailAddress(),
                    "Transfer Confirmed — NexaBank",
                    buildEmailBody(event)));
        }

        if (prefs.isSmsEnabled() && prefs.getPhoneNumber() != null) {
            smsSender.send(prefs.getPhoneNumber(),
                    String.format("NexaBank: %s %s transferred. Ref: %s",
                            event.amount(), event.currency(), event.transactionId()));
        }

        if (prefs.isPushEnabled()) {
            pushSender.send(event.sourceCustomerId(), "Transfer Confirmed",
                    String.format("%s %s sent successfully", event.amount(), event.currency()));
        }
    }

    private String buildEmailBody(TransactionCompletedEvent event) {
        return """
                Your transfer has been processed successfully.

                Amount:    %s %s
                Reference: %s
                Date:      %s

                If you did not authorise this transaction, contact us immediately at 0800-NEXABANK.
                """.formatted(event.amount(), event.currency(), event.transactionId(), event.occurredAt());
    }
}
