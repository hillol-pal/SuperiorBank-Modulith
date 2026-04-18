package com.superiorbank.fraud;

public record FraudScore(double score, RiskLevel riskLevel, String ruleTriggered) {

    public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

    public static FraudScore low() {
        return new FraudScore(0.05, RiskLevel.LOW, null);
    }

    public static FraudScore medium(double score, String rule) {
        return new FraudScore(score, RiskLevel.MEDIUM, rule);
    }

    public static FraudScore high(double score, String rule) {
        return new FraudScore(score, RiskLevel.HIGH, rule);
    }

    public static FraudScore critical(double score, String rule) {
        return new FraudScore(score, RiskLevel.CRITICAL, rule);
    }

    public boolean isHighRisk() {
        return riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL;
    }
}
