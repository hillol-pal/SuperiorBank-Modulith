package com.superiorbank.transaction;

import java.util.UUID;

public record TransactionId(UUID value) {

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(String uuid) {
        return new TransactionId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
