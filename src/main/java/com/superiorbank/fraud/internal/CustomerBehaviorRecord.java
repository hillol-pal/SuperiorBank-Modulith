package com.superiorbank.fraud.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Entity
@Table(name = "customer_behavior_profiles")
@Getter
@NoArgsConstructor
class CustomerBehaviorRecord {

    @Id
    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "avg_transaction_amt", nullable = false, precision = 19, scale = 4)
    private BigDecimal avgTransactionAmt;

    @Column(name = "transaction_count", nullable = false)
    private long transactionCount;

    @Column(name = "last_transaction_at")
    private Instant lastTransactionAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    static CustomerBehaviorRecord newProfile(String customerId) {
        var p = new CustomerBehaviorRecord();
        p.customerId = customerId;
        p.avgTransactionAmt = BigDecimal.ZERO;
        p.transactionCount = 0;
        p.updatedAt = Instant.now();
        return p;
    }

    void recordTransaction(BigDecimal amount, Instant occurredAt) {
        // Running average: newAvg = (oldAvg * n + amount) / (n + 1)
        var newCount = transactionCount + 1;
        this.avgTransactionAmt = avgTransactionAmt
                .multiply(BigDecimal.valueOf(transactionCount))
                .add(amount)
                .divide(BigDecimal.valueOf(newCount), 4, RoundingMode.HALF_UP);
        this.transactionCount = newCount;
        this.lastTransactionAt = occurredAt;
        this.updatedAt = Instant.now();
    }
}
