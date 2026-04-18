package com.superiorbank.notification;

/**
 * Public read-only view of a customer's notification preferences.
 */
public record NotificationPreferences(
        String customerId,
        boolean emailEnabled,
        boolean smsEnabled,
        boolean pushEnabled,
        String emailAddress,
        String phoneNumber
) {}
