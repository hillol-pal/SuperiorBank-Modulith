package com.superiorbank.fraud.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fraud_cases")
@Getter
@NoArgsConstructor
class FraudCaseRecord {

    @Id
    private UUID id;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(name = "fraud_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal fraudScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "rule_triggered", length = 200)
    private String ruleTriggered;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    static FraudCaseRecord of(UUID transactionId, String customerId,
                               double score, String riskLevel, String rule) {
        var rec = new FraudCaseRecord();
        rec.id = UUID.randomUUID();
        rec.transactionId = transactionId;
        rec.customerId = customerId;
        rec.fraudScore = BigDecimal.valueOf(score);
        rec.riskLevel = riskLevel;
        rec.ruleTriggered = rule;
        rec.createdAt = Instant.now();
        return rec;
    }
}
