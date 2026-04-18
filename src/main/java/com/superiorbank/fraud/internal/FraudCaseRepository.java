package com.superiorbank.fraud.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface FraudCaseRepository extends JpaRepository<FraudCaseRecord, UUID> {}
