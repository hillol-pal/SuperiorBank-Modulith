package com.superiorbank.account.internal;

import com.superiorbank.account.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class AccountJpaEntity {

    @Id
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    public static AccountJpaEntity open(String customerId, String currency) {
        var entity = new AccountJpaEntity();
        entity.id = UUID.randomUUID();
        entity.accountNumber = generateIban(currency);
        entity.customerId = customerId;
        entity.balance = BigDecimal.ZERO;
        entity.currency = currency;
        entity.status = AccountStatus.ACTIVE;
        entity.openedAt = Instant.now();
        entity.updatedAt = Instant.now();
        return entity;
    }

    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = Instant.now();
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = Instant.now();
    }

    private static String generateIban(String currency) {
        String countryCode = "GBP".equals(currency) ? "GB" : "EU";
        return countryCode + "29NEXA" + String.format("%016d", (long)(Math.random() * 1_000_000_000_000_000L));
    }
}
