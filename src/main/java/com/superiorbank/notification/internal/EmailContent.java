package com.superiorbank.notification.internal;

record EmailContent(String to, String subject, String body) {

    static EmailContent of(String to, String subject, String body) {
        return new EmailContent(to, subject, body);
    }
}
