package com.superiorbank.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountManagementService accountManagementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountOpeningResponse openAccount(@Valid @RequestBody AccountOpeningRequest request) {

        log.info("Account Open Request : {}, {} , {}",request.customerId(),request.customerEmail(),request.currency());
        Account account = accountManagementService.openAccount(
                request.customerId(),
                request.currency(),
                request.customerEmail()
        );
        return AccountOpeningResponse.from(account);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid account opening request");
        problem.setDetail(ex.getMessage());
        return problem;
    }
}
