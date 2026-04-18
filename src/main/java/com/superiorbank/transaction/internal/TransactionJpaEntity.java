package com.superiorbank.transaction.internal;

import com.superiorbank.transaction.TransactionId;
import com.superiorbank.transaction.TransferRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor
public class TransactionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "source_account_id", nullable = false)
    private UUID sourceAccountId;

    @Column(name = "target_account_id", nullable = false)
    private UUID targetAccountId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "transfer_type", nullable = false, length = 20)
    private String transferType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "fraud_score", precision = 5, scale = 4)
    private BigDecimal fraudScore;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public static TransactionJpaEntity completed(TransactionId id, TransferRequest req, double score) {
        return build(id, req, score, TransactionStatus.COMPLETED);
    }

    public static TransactionJpaEntity blocked(TransactionId id, TransferRequest req, double score) {
        return build(id, req, score, TransactionStatus.BLOCKED);
    }

    private static TransactionJpaEntity build(TransactionId id, TransferRequest req, double score, TransactionStatus status) {
        var e = new TransactionJpaEntity();
        e.id = id.value();
        e.sourceAccountId = req.sourceAccountId().value();
        e.targetAccountId = req.targetAccountId().value();
        e.amount = req.amount();
        e.currency = req.currency();
        e.transferType = req.transferType();
        e.status = status;
        e.fraudScore = BigDecimal.valueOf(score);
        e.createdAt = Instant.now();
        return e;
    }
}
