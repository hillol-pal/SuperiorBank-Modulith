package com.superiorbank.fraud.internal;

import com.superiorbank.fraud.FraudAssessmentService;
import com.superiorbank.transaction.FraudCheckContext;
import com.superiorbank.transaction.FraudGateway;
import com.superiorbank.transaction.FraudVerdict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter: implements the FraudGateway port owned by the transaction module.
 *
 * This class lives in fraud/internal — it is the bridge between the two modules.
 * TransactionService depends only on the FraudGateway interface (its own type).
 * Spring wires this implementation in at runtime. No module boundary is crossed
 * in import statements inside TransactionService.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class FraudGatewayImpl implements FraudGateway {

    private final FraudAssessmentService fraudAssessmentService;

    @Override
    public FraudVerdict verify(FraudCheckContext context) {
        var score = fraudAssessmentService.assess(context);

        if (score.isHighRisk()) {
            log.warn("HIGH RISK transaction detected: account={}, score={}, rule={}",
                    context.sourceAccountId(), score.score(), score.ruleTriggered());
            return FraudVerdict.blocked(score.score(), score.ruleTriggered());
        }

        return FraudVerdict.allowed();
    }
}
