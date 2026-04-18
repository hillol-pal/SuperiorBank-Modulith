package com.superiorbank.loan.internal;

import com.superiorbank.loan.LoanApplication;
import com.superiorbank.loan.LoanId;
import com.superiorbank.loan.LoanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor
public class LoanJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "outstanding_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal outstandingAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }

    public static LoanJpaEntity create(LoanApplication app, LoanStatus status, int creditScore) {
        var e = new LoanJpaEntity();
        e.id = UUID.randomUUID();
        e.customerId = app.customerId();
        e.accountId = app.disbursementAccountId().value();
        e.principalAmount = app.requestedAmount();
        e.outstandingAmount = app.requestedAmount();
        e.interestRate = calculateRate(creditScore);
        e.status = status;
        e.createdAt = Instant.now();
        return e;
    }

    private static BigDecimal calculateRate(int creditScore) {
        if (creditScore >= 800) return new BigDecimal("0.0399");
        if (creditScore >= 700) return new BigDecimal("0.0699");
        if (creditScore >= 650) return new BigDecimal("0.0999");
        return new BigDecimal("0.1499");
    }
}
