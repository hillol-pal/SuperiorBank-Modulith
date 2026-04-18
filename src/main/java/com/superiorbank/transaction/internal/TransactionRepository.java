package com.superiorbank.transaction.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    List<TransactionJpaEntity> findBySourceAccountIdOrderByCreatedAtDesc(UUID sourceAccountId);

    long countBySourceAccountIdAndCreatedAtAfter(UUID sourceAccountId, Instant since);
}
