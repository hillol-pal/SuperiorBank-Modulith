package com.superiorbank.notification.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class SmsNotificationSender {

    void send(String phoneNumber, String message) {
        // Integration point: replace with Twilio, AWS SNS, etc.
        log.info("[SMS] To={} Msg={}", phoneNumber, message);
    }

    void sendFraudAlert(String phoneNumber, String transactionId) {
        log.warn("[SMS-FRAUD-ALERT] To={} TxId={}", phoneNumber, transactionId);
    }
}
