package com.superiorbank.fraud;

import com.superiorbank.fraud.internal.BehavioralAnalyzer;
import com.superiorbank.fraud.internal.RuleEngine;
import com.superiorbank.transaction.FraudCheckContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public API for fraud assessment — usable by admin dashboards or audit APIs.
 * The actual gateway integration with TransactionService goes through FraudGatewayImpl.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FraudAssessmentService {

    private final RuleEngine ruleEngine;
    private final BehavioralAnalyzer behavioralAnalyzer;

    @Transactional(readOnly = true)
    public FraudScore assess(FraudCheckContext context) {
        var ruleScore = ruleEngine.evaluate(context);
        var behaviorScore = behavioralAnalyzer.analyze(context);

        var combined = combine(ruleScore, behaviorScore);

        log.debug("Fraud assessment: account={}, score={}, risk={}",
                context.sourceAccountId(), combined.score(), combined.riskLevel());

        return combined;
    }

    private FraudScore combine(FraudScore rule, FraudScore behavior) {
        double combined = (rule.score() * 0.6) + (behavior.score() * 0.4);

        if (combined >= 0.85) return FraudScore.critical(combined, rule.ruleTriggered());
        if (combined >= 0.65) return FraudScore.high(combined, rule.ruleTriggered());
        if (combined >= 0.40) return FraudScore.medium(combined, rule.ruleTriggered());
        return FraudScore.low();
    }
}
