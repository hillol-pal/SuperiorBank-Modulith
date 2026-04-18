package com.superiorbank.transaction;

public record FraudVerdict(
        boolean approved,
        double score,
        String ruleTriggered
) {
    public static FraudVerdict allowed() {
        return new FraudVerdict(true, 0.0, null);
    }

    public static FraudVerdict blocked(double score, String ruleTriggered) {
        return new FraudVerdict(false, score, ruleTriggered);
    }

    public boolean isHighRisk() {
        return !approved;
    }
}
