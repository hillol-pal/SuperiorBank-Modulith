package com.superiorbank.loan;

import com.superiorbank.account.AccountManagementService;
import com.superiorbank.loan.internal.CreditAssessmentEngine;
import com.superiorbank.loan.internal.LoanJpaEntity;
import com.superiorbank.loan.internal.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CreditAssessmentEngine creditEngine;
    private final AccountManagementService accountService;

    @Transactional
    public LoanId apply(LoanApplication application) {
        // Verify the disbursement account exists and belongs to the customer
        var account = accountService.findById(application.disbursementAccountId())
                .filter(a -> a.customerId().equals(application.customerId()))
                .orElseThrow(() -> new IllegalArgumentException(
                    "Account not found or does not belong to customer: " + application.customerId()));

        var creditScore = creditEngine.assess(application.customerId(), application.requestedAmount());
        var status = creditScore >= 650 ? LoanStatus.APPROVED : LoanStatus.REJECTED;

        var entity = LoanJpaEntity.create(application, status, creditScore);
        loanRepository.save(entity);

        log.info("Loan application {} for customer {} — status: {}, creditScore: {}",
                entity.getId(), application.customerId(), status, creditScore);

        return LoanId.generate();
    }

    @Transactional(readOnly = true)
    public Optional<LoanStatus> getStatus(LoanId loanId) {
        return loanRepository.findById(loanId.value()).map(LoanJpaEntity::getStatus);
    }
}
