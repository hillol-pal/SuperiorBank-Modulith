package com.superiorbank.account;

import java.util.UUID;

public record AccountId(UUID value) {

    public static AccountId of(String uuid) {
        return new AccountId(UUID.fromString(uuid));
    }

    public static AccountId of(UUID uuid) {
        return new AccountId(uuid);
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
