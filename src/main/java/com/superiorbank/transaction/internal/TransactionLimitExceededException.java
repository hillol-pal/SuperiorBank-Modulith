package com.superiorbank.transaction.internal;

class TransactionLimitExceededException extends RuntimeException {
    TransactionLimitExceededException(String message) {
        super(message);
    }
}
