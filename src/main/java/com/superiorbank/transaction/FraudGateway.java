package com.superiorbank.transaction;

/**
 * Port (interface) that the transaction module owns.
 * The fraud module provides the adapter implementation (FraudGatewayImpl).
 *
 * This Ports & Adapters pattern breaks the circular dependency:
 *   transaction → fraud (if we called FraudAssessmentService directly)
 *   fraud → transaction (to listen to TransactionCompletedEvent)
 *
 * With this interface, the dependency is one-directional:
 *   fraud → transaction  (fraud implements this port AND listens to events)
 *   transaction depends on nothing in the fraud module
 */
public interface FraudGateway {

    FraudVerdict verify(FraudCheckContext context);
}
