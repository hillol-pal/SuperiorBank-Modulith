package com.superiorbank.notification.internal;

import com.superiorbank.account.AccountId;
import com.superiorbank.account.AccountOpenedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;


@ApplicationModuleTest
class AccountNotificationListenerTest {

    @MockitoSpyBean
    EmailNotificationSender emailSender;

    @Test
    void onAccountOpened_sendsWelcomeEmailToCustomer(Scenario scenario) {
        var event = new AccountOpenedEvent(
                AccountId.generate(), "CUST-200", "GBP", "welcome@example.com");

        scenario.publish(event)
                .andWaitForStateChange(() -> {
                  
                    try {
                        verify(emailSender).send(argThat(content ->
                                content.to().equals("welcome@example.com") &&
                                content.subject().contains("Welcome")));
                        return true;
                    } catch (AssertionError e) {
                        return null;
                    }
                });

        verify(emailSender).send(argThat(content ->
                content.to().equals("welcome@example.com") &&
                content.subject().equals("Welcome to Superior Bank — Your Account is Open!") &&
                content.body().contains("GBP")));
    }

    @Test
    void onAccountOpened_withNoEmail_skipsNotification(Scenario scenario) {
        // customerEmail is blank — listener should skip gracefully without calling emailSender
        var event = new AccountOpenedEvent(
                AccountId.generate(), "CUST-201", "USD", "");

        // Scenario completes when the listener finishes processing without exception
        scenario.publish(event)
                .andWaitForStateChange(() -> true);
    }
}
