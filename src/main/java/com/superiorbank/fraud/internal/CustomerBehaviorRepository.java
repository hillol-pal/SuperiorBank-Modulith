package com.superiorbank.fraud.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface CustomerBehaviorRepository extends JpaRepository<CustomerBehaviorRecord, String> {
    Optional<CustomerBehaviorRecord> findByCustomerId(String customerId);
}
