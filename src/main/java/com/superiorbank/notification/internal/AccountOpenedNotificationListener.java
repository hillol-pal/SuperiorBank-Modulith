package com.superiorbank.notification.internal;

import com.superiorbank.account.AccountOpenedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to AccountOpenedEvent and sends a welcome email to the new account holder.
 *
 * Boundary rule: this class imports AccountOpenedEvent (account module's public API) — allowed.
 * It does NOT import anything from account/internal — that would break the boundary.
 *
 * @ApplicationModuleListener guarantees this fires AFTER the opening transaction commits,
 * so the welcome email is never sent if account creation rolls back.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class AccountOpenedNotificationListener {

    private final EmailNotificationSender emailSender;

    @ApplicationModuleListener
    void onAccountOpened(AccountOpenedEvent event) {
        if (event.customerEmail() == null || event.customerEmail().isBlank()) {
            log.warn("No email address for customer {}, skipping welcome email", event.customerId());
            return;
        }

        log.info("Sending welcome email to {} for new {} account {}",
                event.customerEmail(), event.currency(), event.accountId());

        emailSender.send(EmailContent.of(
                event.customerEmail(),
                "Welcome to Superior Bank — Your Account is Open!",
                buildWelcomeBody(event)));
    }

    private String buildWelcomeBody(AccountOpenedEvent event) {
        return """
                Dear Customer,

                Congratulations! Your %s account has been successfully opened at Superior Bank.

                Account Reference: %s
                Currency:          %s
                Opened At:         %s

                You can start transacting immediately. For any questions, contact us at
                support@superiorbank.com or call 0800-SUPERIOR.

                Welcome aboard,
                Superior Bank Team
                """.formatted(event.currency(), event.accountId().value(), event.currency(), event.occurredAt());
    }
}
