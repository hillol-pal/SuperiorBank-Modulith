package com.superiorbank.notification.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class PushNotificationSender {

    void send(String customerId, String title, String body) {
        // Integration point: replace with Firebase FCM, Apple APNS, etc.
        log.info("[PUSH] Customer={} Title={}", customerId, title);
    }
}
