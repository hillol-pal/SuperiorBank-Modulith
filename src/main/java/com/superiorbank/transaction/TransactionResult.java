package com.superiorbank.transaction;

public record TransactionResult(
        TransactionId transactionId,
        Status status,
        String reason
) {
    public enum Status { SUCCESS, BLOCKED, FAILED }

    public static TransactionResult success(TransactionId id) {
        return new TransactionResult(id, Status.SUCCESS, null);
    }

    public static TransactionResult blocked(TransactionId id, String reason) {
        return new TransactionResult(id, Status.BLOCKED, reason);
    }

    public static TransactionResult failed(TransactionId id, String reason) {
        return new TransactionResult(id, Status.FAILED, reason);
    }

    public boolean isSuccess() { return status == Status.SUCCESS; }
    public boolean isBlocked() { return status == Status.BLOCKED; }
}
