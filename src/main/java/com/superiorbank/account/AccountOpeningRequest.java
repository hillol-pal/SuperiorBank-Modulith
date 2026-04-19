package com.superiorbank.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountOpeningRequest(
        @NotBlank(message = "Customer ID must not be blank") String customerId,
        @NotBlank(message = "Currency must not be blank") String currency,
        @NotBlank(message = "Email address must not be blank") @Email(message = "Must be a valid email address") String customerEmail
) {}
