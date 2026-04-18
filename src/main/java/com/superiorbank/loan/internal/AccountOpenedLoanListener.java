package com.superiorbank.loan.internal;

import com.superiorbank.account.AccountOpenedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * When a new account is opened, the loan module initialises the customer's
 * credit profile so they're ready for loan applications immediately.
 */
@Component
@Slf4j
class AccountOpenedLoanListener {

    @ApplicationModuleListener
    void onAccountOpened(AccountOpenedEvent event) {
        // In production: trigger credit bureau pre-check, set initial loan eligibility
        log.info("Initialising loan eligibility profile for new customer: {}", event.customerId());
    }
}
