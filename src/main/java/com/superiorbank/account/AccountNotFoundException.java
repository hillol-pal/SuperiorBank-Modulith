package com.superiorbank.account;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(AccountId accountId) {
        super("Account not found: " + accountId);
    }

    public AccountNotFoundException(String accountNumber) {
        super("Account not found for number: " + accountNumber);
    }
}
