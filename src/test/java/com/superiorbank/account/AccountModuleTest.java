package com.superiorbank.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ApplicationModuleTest
class AccountModuleTest {

    @Autowired
    AccountManagementService accountManagementService;

    @Test
    void openAccount_publishesAccountOpenedEvent(Scenario scenario) {
        scenario.stimulate(() -> accountManagementService.openAccount("CUST-100", "GBP", "cust100@example.com"))
                .andWaitForEventOfType(AccountOpenedEvent.class)
                .toArriveAndVerify(event -> {
                    assertThat(event.customerId()).isEqualTo("CUST-100");
                    assertThat(event.currency()).isEqualTo("GBP");
                    assertThat(event.customerEmail()).isEqualTo("cust100@example.com");
                    assertThat(event.accountId()).isNotNull();
                    assertThat(event.occurredAt()).isNotNull();
                });
    }

    @Test
    void openAccount_returnsActiveAccountWithZeroBalance() {
        Account account = accountManagementService.openAccount("CUST-101", "EUR", "cust101@example.com");

        assertThat(account.isActive()).isTrue();
        assertThat(account.balance()).isEqualByComparingTo("0.00");
        assertThat(account.currency()).isEqualTo("EUR");
        assertThat(account.customerId()).isEqualTo("CUST-101");
        assertThat(account.accountNumber()).isNotBlank();
    }

    @Test
    void openAccount_withUnsupportedCurrency_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> accountManagementService.openAccount("CUST-102", "JPY", "cust102@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported currency: JPY");
    }

    @Test
    void openAccount_withBlankCustomerId_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> accountManagementService.openAccount("", "GBP", "cust@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer ID must not be blank");
    }

    @Test
    void findById_returnsEmptyForUnknownAccount() {
        var unknown = AccountId.generate();
        assertThat(accountManagementService.findById(unknown)).isEmpty();
    }
}
