package com.superiorbank.loan;

import com.superiorbank.account.AccountId;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplication(
        @NotNull String customerId,
        @NotNull AccountId disbursementAccountId,
        @NotNull @DecimalMin("500.00") BigDecimal requestedAmount,
        @NotNull String purpose  // PERSONAL, HOME, AUTO, BUSINESS
) {}
