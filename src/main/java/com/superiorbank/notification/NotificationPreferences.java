package com.superiorbank.notification;


public record NotificationPreferences(
        String customerId,
        boolean emailEnabled,
        boolean smsEnabled,
        boolean pushEnabled,
        String emailAddress,
        String phoneNumber
) {}
