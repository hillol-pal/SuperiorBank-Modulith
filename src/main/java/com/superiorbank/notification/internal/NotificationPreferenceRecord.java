package com.superiorbank.notification.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
@Getter
@NoArgsConstructor
class NotificationPreferenceRecord {

    @Id
    private UUID id;

    @Column(name = "customer_id", unique = true, nullable = false, length = 50)
    private String customerId;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled;

    @Column(name = "sms_enabled", nullable = false)
    private boolean smsEnabled;

    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled;

    @Column(name = "email_address", length = 255)
    private String emailAddress;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
}
