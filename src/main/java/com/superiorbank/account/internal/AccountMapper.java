package com.superiorbank.account.internal;

import com.superiorbank.account.Account;
import com.superiorbank.account.AccountId;

public final class AccountMapper {

    private AccountMapper() {}

    public static Account toDomain(AccountJpaEntity entity) {
        return new Account(
                AccountId.of(entity.getId()),
                entity.getAccountNumber(),
                entity.getCustomerId(),
                entity.getBalance(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getOpenedAt()
        );
    }
}
