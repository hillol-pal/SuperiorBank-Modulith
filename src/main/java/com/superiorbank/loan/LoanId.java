package com.superiorbank.loan;

import java.util.UUID;

public record LoanId(UUID value) {

    public static LoanId generate() {
        return new LoanId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
