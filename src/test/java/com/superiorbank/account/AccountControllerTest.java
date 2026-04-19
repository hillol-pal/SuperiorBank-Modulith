package com.superiorbank.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void openAccount_withValidRequest_returns201WithAccountDetails() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "CUST-001",
                                    "currency": "GBP",
                                    "customerEmail": "jane.doe@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").isNotEmpty())
                .andExpect(jsonPath("$.accountNumber").isNotEmpty())
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.openedAt").isNotEmpty());
    }

    @Test
    void openAccount_withEurCurrency_returns201() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "CUST-002",
                                    "currency": "EUR",
                                    "customerEmail": "max.m@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void openAccount_withUnsupportedCurrency_returns400() throws Exception {
        // AccountValidator rejects currencies outside GBP/EUR/USD — mapped to 400 via @ExceptionHandler
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "CUST-003",
                                    "currency": "JPY",
                                    "customerEmail": "taro@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Unsupported currency: JPY"));
    }

    @Test
    void openAccount_withMissingCustomerId_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "currency": "GBP",
                                    "customerEmail": "jane@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void openAccount_withBlankCustomerId_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "   ",
                                    "currency": "GBP",
                                    "customerEmail": "jane@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void openAccount_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "CUST-004",
                                    "currency": "GBP",
                                    "customerEmail": "not-an-email"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void openAccount_withMissingEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "CUST-005",
                                    "currency": "GBP"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
