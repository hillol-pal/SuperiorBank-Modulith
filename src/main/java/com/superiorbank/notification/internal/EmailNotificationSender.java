package com.superiorbank.notification.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class EmailNotificationSender {

    void send(EmailContent content) {
        // Integration point: replace with JavaMailSender, SendGrid, AWS SES, etc.
        log.info("[EMAIL] To={} Subject={}", content.to(), content.subject());
    }

    void sendFraudAlert(String emailAddress, String transactionId) {
        log.warn("[EMAIL-FRAUD-ALERT] To={} TxId={}", emailAddress, transactionId);
    }
}
